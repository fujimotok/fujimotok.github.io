{:title "Widnowsシェル拡張開発"
 :tags  ["Windows"]
 :layout :post
 :toc true}

Widnowsシェル拡張の開発について調べたのでメモ。

## Widnowsシェル拡張とは
エクスプローラーに機能を追加するもの。  
エクスプローラーの持ってるフックポイントでDLLを呼んでくれる。  

## 開発するには
[シェル拡張機能ハンドラーの作成](https://learn.microsoft.com/ja-jp/windows/win32/shell/handlers)が公式説明。  
COMオブジェクトのDLLを作る必要がある。  
C++でしか説明がない。  

[Github - sharpshell](https://github.com/dwmkerr/sharpshell)というのを使えばC#で書けて、クラスをExtendするだけでお手軽実装できる。

## sharpshellでの開発
[Github - sharpshell](https://github.com/dwmkerr/sharpshell)にサンプル用意されてるのでこれを動かしてみるのがいい。

拡張機能の開発自体は、Nugetで参照追加して、必要なクラスをExtendすればいいが、COMオブジェクトを登録するのが面倒。  
なので、`Tools/ServerManager`が用意されている。

こいつを使うと
- COMオブジェクトの登録（Serverのインストールと呼ばれてる）
- エクスプローラーの代わりのデバッグ用エクスプローラ（Test Shellと呼ばれている）
ができる。

Test ShellはServerのインストール無しでも動くと書いていたが、例外が出て動かなかった。  
動いた手順は以下。
1. Server Manager起動
2. File > Load Server でDLLのパス選択
3. リストに登録される
4. リスト選択状態で Server > Install を実行
5. 「Test Server in Test Shell」ボタンでデバッグ用エクスプローラ起動
6. 動作確認

Test Shell のバグなのかわからないが、例外が出まくるため、フォルダの一番上の1アイテムしか表示されない。  
仕方がないので、確認用には1ファイルしかないフォルダを使うことにした。

ただ、Server Managerでインストール実行したら、すぐに本チャンのエクスプローラにも適用されていた。  
（プレビュー機能野を試したが、こいつは読み込まれるチャンスが後にあるからか？）

もう1個の注意点はインストールしたDLLを再ビルドするとき、使用中でビルド失敗する。  
ビルド時にはアンインストールやエクスプローラを終了しないといけないようだ。  
Win11のタブになってる場合は、一度プレビュー機能使ったタブだけ閉じたらOKだった。

特に明文化されてないっぽいが、1つのDLLに複数のHandlerクラスは登録できないようだ。  
拡張子ごとにHandlerクラスを分けてみたが駄目だった。  
1つのHandlerクラス内で拡張子でDispachするしかないみたい。

## 終わりに
sharpshell使うと意外と簡単にシェル拡張が実装できそうなことが分かった。

