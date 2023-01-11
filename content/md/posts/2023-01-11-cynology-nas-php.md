{:title "Synology NAS で PHP を使えるようにする"
 :tags ["PHP"]
 :layout :post
 :toc true}

Synology NASでPHPを使えるようにした。  
純粋にPHPを触る情報があまり見つからなかったのでメモ。

## 目的
SynologyのNAS（DS220j）を使って、自炊した本のpdfを貯めてる。  
pdfを閲覧するのために、Windowsだとエクスプローラー、iPhoneだとファイルアプリでNASに入る必要がある。  
これをブラウザから見られるようにしたい。

## Webサーバとスクリプティング
SynologyのNASは、ブラウザアプリでデスクトップ環境のようなモノを持っていて、  
パッケージセンターというGUIで機能をDLできるようになっている。

Webサーバ機能は`Web Station`という名前のパッケージ。  
Webサーバはデフォルトで`Nginx`がインストールされる。  
こいつのバックエンドで使えるスクリプティングには`PHP`と`Python`が使える。

せっかくなので使ったことのない`PHP`を使ってみることにした。

## PHPのセットアップ
ネットで調べると、sshログインしてコマンドラインで直接設定する方法とかがあったが、  
せっかくNAS使ってる意味がないので、なるだけSynologyの提供してるUIで完了できる方法を探した。

[ここ](https://linuxhint.com/use-synology-web-station/)を参考に、`index.php`を置くことで、`<NASの名前>.local`にアクセスで、このPHPが呼ばれるようになった。

## PHPのコード
まずは、HelloWorld的なので以下を試した。
```php
<?php
    phpinfo();
 ?>
```

Reactとかに慣れすぎて忘れていたが、PHPは評価された結果が展開されるようなモノではなくて、`echo`とかで出力した結果がHTMLに吐かれるんだった。  
Cで書いたCGIとかを思い出す。

やりたかった、PDFの返却は[ここ](https://tips.recatnap.info/laboratory/detail/output_to_browser_by_php)を参考にした。
```php
<?php
    $filename = 'test.pdf';
    header('Content-Type: application/pdf');
    header('Content-Disposition: inline; filename="' . basename($filename) . '"');
    header('Content-Length: ' . filesize($filename));
    readfile($filename);
?>
```

## ルーティングどうするか
欲しい機能は以下。
- URLで欲しいファイルを指定したら、PDFをViewする
- ルートの時はファイル一覧を表示する

ルーティングが必要だが、`Nginx`のコンフィグいじらないと無理っぽい。

あまりきれいではないがクエリパラメータで泥臭くやるか。


## あとがき
LaravelとかならSPA的なのやりやすいんだろうな。  
もうWebアプリ考えるときはSPAを前提に考えてしまうな。
