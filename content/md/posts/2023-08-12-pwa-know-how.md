{:title "PWAのノウハウ"
 :tags  ["PWA"]
 :layout :post
 :toc true}

PWAのアプリを作るにあたってのノウハウが溜まってきたので記録する。

## iOS対応
### 最低限の準備
iOSでPWAとして認識させるには、以下のメタタグが必要。
```
    <meta name="mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-title" content="your app name">
```

そして、マニフェストファイルが必要。
```
{
  "name": "your app name",
  "short_name": "your app name",
  "description": "your app description",
  "start_url": "./",
  "display": "standalone"
}
```

マニフェストで`"display": "standalone"`を指定するのと、以下を指定することで、全画面アプリとなる。
```
    <meta name="apple-mobile-web-app-status-bar-style"
          content="black-translucent">
```

[pwacompat](https://www.npmjs.com/package/pwacompat)を使ってると、`black-translucent`上手くいかなかった。


### ホームアイコンの指定
アイコン対応するには`apple-touch-icon`にpngを指定するだけでOK。
```
    <link
      rel="apple-touch-icon"
      type="image/png"
      sizes="180x180"
      href="./icons/apple-touch-icon.png"
    />
```

### スプラッシュの指定
スプラッシュを指定するのが一番面倒。  
それぞれの機種に対応した画像サイズを用意する必要がある。  
そして、`apple-touch-startup-image`に指定するのだが、  
対象の機種のときだけ有効になるようにメディアクエリが必要となる。
```
<link
  rel="apple-touch-startup-image"
  href="./iphone5_splash.png"
  media="(device-width: 320px) and (device-height: 568px) and (-webkit-device-pixel-ratio: 2)" />
```

[このサイト](https://appsco.pe/developer/splash-screens)使うと、必要な画像と、タグの書き方を教えてくれる。  
しかし、iPhoneXRは指定が間違っている。  
正しくは、`(device-width: 414px) and (device-height: 896px) and (-webkit-device-pixel-ratio: 2)`。


さらに、ダークモードの場合にスプラッシュの色を変えたい場合には、以下のようにメディアクエリ指定する。
```
<!-- ダークモード用 -->
<link
  rel="apple-touch-startup-image"
  href="./iphone5_splash.png"
  media="(prefers-color-scheme: dark) and (device-width: 320px) and (device-height: 568px) and (-webkit-device-pixel-ratio: 2)" />

<!-- ライトモード用 -->
<link
  rel="apple-touch-startup-image"
  href="./iphone5_splash.png"
  media="(not (prefers-color-scheme: dark)) and (device-width: 320px) and (device-height: 568px) and (-webkit-device-pixel-ratio: 2)" />
```

スプラッシュの画像はインストール時に固定されてしまうので、  
インストール時点でダークモードで、後からライトモードにしても、ダークモードの画像が出る。

## Android
### 最低限の準備
追加のメタタグは不要で、マニフェストファイルがあればよい。
```
{
  "name": "your app name",
  "short_name": "your app name",
  "description": "your app description",
  "start_url": "./",
  "display": "standalone"
}
```

マニフェストで`"display": "fullscreen"`を指定すると全画面アプリとなる。  
（iOSでは`"display": "fullscreen"`指定しても`"display": "standalone"`扱いとなる）  
が、[Material UI](https://mui.com/material-ui/)のダイアログとテキスト入力でおかしな動作となる。

仕方がないので、`"display": "standalone"`とし、  
ステータスバーの色を、背景色と合わせて疑似フルスクリーンとした。

### ステータスバーの色
`"display": "standalone"`では、ステータスバーに色がつく。  
タイトルバーと合わせることでネイティブっぽく見せる仕組み。

色を指定するには、メタタグで`theme-color`を指定する。  
これも、ダークモード対応のため、メディアクエリで切り替える。
```
    <meta name="theme-color" media="(prefers-color-scheme: light)" content="#FFF">
    <meta name="theme-color" media="(prefers-color-scheme: dark)" content="#000">
```

マニフェストにも、`theme_color`があるが、効かなかった。

### ホームアイコンの指定
アイコン対応するにはマニフェストの`icons`にpngを指定するだけでOK。
```
{
  "icons": [
    {
      "src": "./icons/android-chrome-192x192.png",
      "sizes": "192x192",
      "type": "image/png"
    },
    {
      "src": "./icons/android-chrome-512x512.png",
      "sizes": "512x512",
      "type": "image/png"
    }
  ]
}
```

### スプラッシュの指定
マニフェストに指定の、`icons`と`background_color`でスプラッシュ画像が自動生成される？
こちらも、インストール時に画像固定される？

## さいごに
Androidで`"display": "fullscreen"`が使えるように、MaterialUIを修正できたら、fullscreenを使いたい。
