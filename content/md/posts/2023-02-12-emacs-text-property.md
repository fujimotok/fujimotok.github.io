{:title "Emacsのテキストプロパティについて"
 :tags ["Emacs"]
 :layout :post
 :toc true}

Emacsのmode-lineを触っていてハマったので整理。  
テキストプロパティが分かれば、色付けたり、マウスインタラクション付けたりできて便利。

## Emacsにおけるstringオブジェクトの変な仕様
Emacsのstringには、平文情報以外に、書式情報も含まれている。  
例えば、`all-the-icon`でmode-lineに表示するアイコンはこんな感じ。
```lisp
(all-the-icons--icon-info-for-buffer)
;;=> #("" 0 1 (face (:family "file-icons" :height 1.0 :inherit all-the-icons-orange) font-lock-face (:family "file-icons" :height 1.0 :inherit all-the-icons-orange) display (raise -0.1) rear-nonsticky t))
(type-of (all-the-icons--icon-info-for-buffer))
;;=> string
```

stringは文字、start、end、プロパティで構成されていることがわかる。

## listとalistとplist
テキストプロパティを制御しようと思ってハマったので整理する。  
Emacsで扱うデータ構造は、基本は`list`。これはわかりやすい。 
```lisp
'(1 2 3)
```

次に、key-valueストアとして使える`list`が`alist`（連想配列）。
仕組みとしては、コンスセルのリストになっている。
```lisp
'((one . 1)
  (two . 2)
  (three . 3))
```

最後に、今回ハマった`plist`（属性リスト）。  
こちらは`alist`と違って、`key`と`value`をそのまま順番に連結したものとなる。  
別に縛りではないが、キーワード（コロン付きの）シンボルが使われる傾向にある。
```lisp
'(:one 1
  :two 2
  :three 3)
```

これら3つのリストごとに操作関数が分かれている。

## テキストプロパティの操作
詳しくは[テキストプロパティの変更](https://ayatakesi.github.io/lispref/24.5/html/Changing-Properties.html)。

テキストプロパティの操作関数は、`buffer`もしくは`string`を受け入れるようになっている。  
そして、操作は副作用を起こす形で実行される。  
`buffer`に対しては、この仕様は便利だが、`string`に対しては不便。  
なので、`properize()`という関数があって、これがテキストプロパティを反映した`string`を返すようになっている。

`properize()`の厄介なところは、元のテキストプロパティが全部消えてしまうこと。  
`get-text-property()`で元のテキストプロパティを取り出しておく必要がある。  
何かもっといい方法があれば知りたい。

## あとがき
日本語翻訳されたドキュメントもあって、ネットの検索でも引っかかるのでヒントは多かったが、結局コードを動かして理解するのが一番早かった。
