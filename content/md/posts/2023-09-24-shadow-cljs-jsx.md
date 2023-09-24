{:title "Shadow-cljs で JSXを使う"
 :tags  ["ClojureScript"]
 :layout :post
 :toc true}

Shadow-cljs で JSXを使いたくて調べた結果。

## cljsでjs呼び出す方法
```clojure
(ns app.sample
  (:require ["/js/MyComponent" :refer (MyComponent)]   ;; (1)
            ["../js/MyComponent" :refer (MyComponent)] ;; (2)
            ["react" :as d]))                          ;; (3)
```

1. ルートから指定： `"/aaa/bbb.js"`
2. 相対パス指定： `"../aaa/bbb.js"`
3. `npm install`したパッケージ： `"package-name"`

ルートは`shadow-cljs.edn`の`:source-paths ["path"]`から決まる。

## JSXは直接読めない
ClojureScriptのデフォルトではJSXに対応しない。  
JSXは、`babel`などによってjsに変換されて使われている。

[Shadow-cljs公式ガイド](https://shadow-cljs.github.io/docs/UsersGuide.html#_javascript_dialects)にやり方書いてある。

Shadow-cljsで使うなら、`.jsx`は`:source-paths ["path"]`と別の場所において、  
`babel path/to/.jsx --out-dir path/to/source-path --watch` でビルドして、生成物のdirを`:source-paths ["path"]`に設定しよう。

babelでJSXを変換するには以下をインストール。
```sh
npm install --save-dev @babel/core @babel/cli
npm install --save-dev @babel/plugin-transform-react-jsx
```

## おわりに
JSXを読めるようになったが、使いたかったモジュールは依存関係がうまくいかず、あきらめた。

