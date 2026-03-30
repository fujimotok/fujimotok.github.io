{:title "Volumio4をBluetoothスピーカーにする"
 :tags  ["Linux"]
 :layout :post
 :toc true}

前に見つけたpluseaudio使わなくてもいい方法見つけたので。

## 前置き
Volumio のプレミアム機能でBluetoothスピーカー機能がある。  
それをプレミアムなしで使えるようにする手順。  
volumio 4 bookworm で実施しているが、  
おそらく、プレミアムでBT使えるようになったバージョンならいけるはず。

## BlueALSA のセットアップと確認
`systemctl status bluealsa` でBlueALSAが動いてるか確認。

```
volumio@volumio:~$ systemctl status bluealsa
● bluealsa.service - BlueALSA service
     Loaded: loaded (/lib/systemd/system/bluealsa.service; enabled; preset: enabled)
     Active: active (running) since Thu 2026-03-26 20:36:10 JST; 2 days ago
       Docs: man:bluealsa(8)
   Main PID: 861 (bluealsa)
      Tasks: 4 (limit: 1385)
        CPU: 610ms
     CGroup: /system.slice/bluealsa.service
             └─861 /usr/bin/bluealsa -S -p a2dp-source -p a2dp-sink
```

サービス登録済み、かつ、a2dp-sink（bluetoothレシーバ機能）有効化済みだったので追加のインストールはしてない。


## ペアリング
`bluetoothctl`を実行。プロンプトが`[bluetooth]`になる。  
以下のコマンドを順次実行。

`power on` : Bluetoothアダプタの電源を入る。  
`agent on` : ペアリング時の認証（パスキー確認など）エージェントを使用。  
`default-agent` : デフォルトのエージェントを設定。  
`discoverable on`: 検索可能に設定。  
`pairable on` : ペアリングを許可。

ここでスマホからVolumioを探して接続。Volumio側で許可する。  
以降操作不要にするため`trust [MACアドレス]`を実行。  
これ以上ペアリングしないので`discoverable off`。

これで、ペアリングは終了。`exit`で終了する。

## 再生するデバイスの番号確認（pcm）
Volumioで音再生してデバイスの番号を確認。  
`pcmC<X>D<Y>p`の番号を次項のaplayの`hw:X,Y`に使用。

```
volumio@volumio:~$ sudo fuser -v /dev/snd/pcm* （再生中）
                     USER        PID ACCESS COMMAND
/dev/snd/pcmC3D0p:   mpd        7207 F.... mpd
volumio@volumio:~$ sudo fuser -v /dev/snd/pcm* （停止中）

volumio@volumio:~$
```

## aplay でレシーバを試す
BlueALSAの音を再生デバイスにつなぐためのスクリプトを作成する。  
`sudo nano /usr/local/bin/bt-audio-start.sh` でファイル作成。

```sh
#!/bin/bash
DAC="hw:3,0" # 前項で調べた番号に設定

# release mpd
/usr/local/bin/volumio stop
sleep 3

# BlueALSA-aplay restart
/usr/bin/pkill bluealsa-aplay
/usr/bin/bluealsa-aplay -D $DAC
```

再生デバイスが競合するので、先にVolumioの音は止めておくこと。  
`sudo /usr/local/bin/bt-audio-start.sh` で手動でbluealsa-aplay起動。  
これで音流せるはず。

終わったら`pkill bluealsa-aplay`する。Volumioから音出せなくなるため。

## bluealsa-aplay有効化を自働化
bluetooth接続・切断時にフックするスクリプトをudevに登録。  
`sudo nano /etc/udev/rules.d/99-bluetooth-audio.rules` でファイル作成。

```sh
# bluetooth connection detection
ACTION=="add", SUBSYSTEM=="bluetooth", RUN+="/usr/bin/systemd-run --unit=bt-audio-start /usr/local/bin/bt-audio-start.s>
ACTION=="remove", SUBSYSTEM=="bluetooth", RUN+="/usr/bin/pkill bluealsa-aplay"
```

`bt-audio-start.sh`は`systemd`経由で実行する。  
そうしないと、`bluealsa-aplay`が親が即終了して共に死ぬので。

## 終わりに
AIに聞いて得た回答を実践しただけだが、実績ができたので記録しておく。





