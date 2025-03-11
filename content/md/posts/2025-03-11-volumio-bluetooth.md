{:title "Volumio3をBluetoothスピーカーにする"
 :tags  ["Linux"]
 :layout :post
 :toc true}

やり方が分からなくて4年くらい放置してたのに改めてチャレンジ。

## 前置き
自分の使ってるVolumio動かしているラズパイは3Bで、Bluetooth対応してる。

「ラズパイ+Bluetoothスピーカー」で検索すると、  
ラズパイをBluetoothスピーカーに接続するケースとかが検索に引っかかる。

やりたいのはラズパイ側をスピーカーとして、スマホ等をBluetoothで繋ぎたい。

ラズパイ側をスピーカーにする例も見つかったが、Volumioでの実績を調べると、  
いろいろ無理みたいな検索結果が出て、諦めていた。

## 手がかりを見つける
[volumioをBluetoothスピーカー化](https://studio.sites-mall.com/blog/entry/2021013001.html) そのままのが出てきた。

Volume2ではBluez系をapt installしても失敗してしまったため、Volumio3に上げることを決意。  
Volumio-3.795-2025-02-27-piを使用。

pulseaudioが何者かよくわかってないので調べる。  
[Volumio を pulseaudio に対応させる備忘録](https://www.labohyt.net/blog/server/post-4545)

上記サイトによると、Bluetoothの再生はpluseaudioで行う。  
pluseaudioはALSAを使って音を出力する。

Volumioの再生で使用してるmpdはALSAにダイレクトにつながっている。  
これは、ロスを減らして再生パフォーマンスを良くするため。

Bluetoothとmpdを共存させたかったら両方の出力先をpulseaudioにすると良い。

これだとALSAの実行権が競合してできない。
```
             mpd → ALSA
BT → pluseaudio → ALSA
```

これならOK。
```
mpd → pluseaudio → ALSA
BT  → pluseaudio ↗
```

しかし、Volumioについてくるmpdはpluseaudioに対応してないらしい。

## 一旦Volumioを3に上げる
Volumio3の環境を作り、`mpd --version`すると、`Output plugins: pulse`が見つかった。  
つまり、pulseaudioに対応したmpdが最初から入ってた。

また、Bluetooth関連のパッケージbluezや、pulseaudioとpulseaudioのBluetooth関連モジュールも最初から入っていた。

Volumio3ならmpdの出力をpluseaudioにして、pulseaudioをデフォルトで使う構成にすれば実現できそうとわかった。

## pulseaudioの設定
### システムサービスとして起動
pulseaudioは通常はデスクトップでの使用を想定しているため、ログインユーザのプロセスで起動する。  
Volumioの場合ログインせずに使用するため、システムサービス化が必要。

[PulseAudioをsystem serviceとして動かす](https://qiita.com/fujiba/items/9f90e90d5e9366ec8483)  
上記設定するだけでOK

### pulseaudioでBluetoothの設定をする
[Raspberry Pi で Bluetooth レシーバー (オーディオ:A2DP) を作成するための設定方法解説](https://ifritjp.github.io/documents/singleboard/bluetooth-a2dp-receiver/)  
上記がかなり参考になる  
`apt install pulseaudio-module-bluetooth`して、
`/etc/pulse/default.pa`に設定を書く

以上を行うと、[volumioをBluetoothスピーカー化](https://studio.sites-mall.com/blog/entry/2021013001.html)と同じことになる。

途中サービス起動引数の`--disallow-module-loading`があるとBluetoothのsourceが出てこない問題があったので消した。

## 最終状態
systemdサービスの設定ファイル作成。  
`/etc/systemd/system/pulseaudio.service`
```
[Unit]
Description=Sound Service

[Service]
# Note that notify will only work if --daemonize=no
Type=notify
ExecStart=/usr/bin/pulseaudio --daemonize=no --exit-idle-time=-1 --disallow-exit=true --system
Restart=always

[Install]
WantedBy=default.target
```

pulseaudioの設定は3か所編集する。
1. mpdからtcpで接続してくることを許可する  
`load-module module-native-protocol-tcp auth-ip-acl=127.0.0.1`

2. Bluetoothモジュールを必ず読み込むように設定する  
これはやらなくてもいいかも。

3. 出力をalsaにするように設定する  
`load-module module-alsa-sink device=hw:<N>,0`  
`<N>`は`aplay -l`で調べる。

`/etc/pulse/default.pa`
```
（略）
### Network access (may be configured with paprefs, so leave this commented
### here if you plan to use paprefs)
#load-module module-esound-protocol-tcp
#load-module module-native-protocol-tcp
load-module module-native-protocol-tcp auth-ip-acl=127.0.0.1
#load-module module-zeroconf-publish

（略）

### Automatically load driver modules for Bluetooth hardware
#.ifexists module-bluetooth-policy.so
load-module module-bluetooth-policy
#.endif

#.ifexists module-bluetooth-discover.so
load-module module-bluetooth-discover
#.endif

（略）

# 最後に追加
load-module module-alsa-sink device=hw:2,0
```

### 動作確認
再起動して

`$ sudo pactl list sources short`
でBluetoothのsourceが生えてくること

`$ sudo pactl list sinks short`
でALSAのsinkが生えてくること確認

## mpdでpluseaudioを使う設定
[Music Player Daemon の出力を PulseAudio にする方法](https://zenn.dev/toru3/articles/dcafb713689eed)を参考にした。

しかし、`/etc/mpd.conf`はvolumioが書き換えてしまう。

`/volumio/app/plugins/music_service/mpd/mpd.conf.tmpl`  
こいつがvolumioが書き換えるときの元となるファイル。  
これを編集する。

ALSAの設定の手前にpluseを設定。
```
（略）

# Audio Output ################################################################
audio_output {
    type            "pulse"
    name            "pulse"
    server          "127.0.0.1"
}

${sox}

audio_output {
                type            "alsa"
                name            "alsa"
                device          "${device}"
                dop                     "${dop}"
                ${mixer}
                ${format}
                ${special_settings}

}
```

## 終わりに
pulseaudio経由することでノイズとか音質が悪化とかの現象起こる可能性があるみたいな情報もあったが、  
実際には何も起きず、とても快適になった。




