{:title "Emacsで書式付きコピーをWidnowsで行う"
 :tags  ["Emacs" "Windows"]
 :layout :post
 :toc true}

Emacsで書式付きコピーをしたかった。Windowsでのやり方だけ調べた。

## 書式付きコピーをしたい背景
MS OfficeやOneNoteなどにコードをコピペするときに、シンタックスハイライトしたい。  
ネットで調べたら、VSCodeとかからコピーするとできるとある。  
Emacsでやりたい。  

## 書式付きコピーするには
リッチテキストのような形で持ってると想定。  
Windowsのクリップボードについて調べたらこんな記事が。  
[Windows のクリップボードの話をしよう](https://zenn.dev/creanciel/articles/windows-clipboard)

HTMLで保持してるらしい。  
確かに書式を指定するのにHTMLは都合がいい。

つまり、EmacsからHTMLでバッファの内容を出力すれば実現可能ということだ。

## バッファの内容をHTMLで出力
調べたらすでにそのような拡張が存在している。  
[Htmlize](https://www.emacswiki.org/emacs/Htmlize)

elpaにもあるので、`package-install`で導入完了。

## クリップボードにコピー
クリップボードにHTML形式で保持するには、フォーマットをHTMLに指定するだけでは足りないらしい。  
クリップボードに渡すテキストデータには2つのルールがある。

まず、独自のヘッダ部分。
```
Version:0.9
StartHTML:0000000217
EndHTML:0000005686
StartFragment:0000000361
EndFragment:0000005659
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!-- Created by htmlize-1.57 in inline-css mode. -->
<html>
  <head>
    <title>clipboard-html.el</title>
  </head>
  <body style="color: #f8f8f2; background-color: transparent;"><!--StartFragment-->

(省略)
```

Version, StartHTML, EndHTML, StartFragment, EndFragment が必要。  
StartHTML, EndHTML はHTMLタグで囲われた位置を示すバイト数を10進数10桁で記入。  
StartFragment, EndFragment は後述の`<!--StartFragment-->`と`<!--EndFragment-->`で囲われた位置を示すバイト数を10進数10桁で記入。  

次に、`<!--StartFragment-->`と`<!--EndFragment-->`。  
これは、HTMLの中からペーストされる箇所を指定するために使用される。  
たいていは全体を利用したいから、BODYタグで囲われたとこに入れる。

これら2つのルールを満たすと書式付きコピーができる。

この辺の処理をHTML流し込んだら自動で行ってクリップボードにコピーまで行うのをPowershellで書いた。  
[GitHub - clipboard-html.ps1](https://github.com/fujimotok/emacs-utils/blob/main/clipboard-html.ps1)

## Htmlizeが生成するHTMLだと色や改行が正しく反映されない
Htmlizeが生成するコードは、PREブロックを使って空白と改行をそのままで表示するように指示してる。  
一方、VSCodeとかではHTML特殊文字で空白と改行を表現しているため問題が起きない。

Htmlizeが吐いたHTMLを自力で置換して対応した。  
空白をすべて置換すると、HTMLで使ってるタグの属性とかの空白も置き換えてしまうので、  
文頭の空白のみを対象とした。

また、色についても、OneNoteだと大丈夫だがMS Officeはダメなことがあった。  
どうもHeadのCSSでスタイル指定はダメで、インラインのスタイル指定じゃないといけない。  
これは、Htmlizeのプロパティ`htmlize-output-type`を`'inline-css`に指定すればOK。

## 完成品
[GitHub - clipboard-html.el](https://github.com/fujimotok/emacs-utils/blob/main/clipboard-html.el)

## 終わりに
今回はGitHub Copilot使ってelisp書いたが、なかなかいい感じだった。



