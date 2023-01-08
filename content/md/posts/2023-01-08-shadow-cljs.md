{:title "Shadow cljs を思い出す"
 :layout :post
 :tags  ["ClojureScript"]
 :toc true}
 
# 背景
前に作ったClojureScriptのコードをいじろうとして、やり方忘れていたので再勉強

# プロジェクト構成
`create-cljs-app`を使って作ったやつだった  
`shadow-cljs.edn`がプロジェクトファイルで、ここに依存関係とかプロジェクト構成とか入ってる  
`card`とか`e2e`とか理解してないものがあるので調べた

# devcards
[詳しくはここ](https://qiita.com/ayato_p/items/17f3e0677e16470fcd77)  
ただのカードコンポーネントではない  
UIの状態をテストケースとして書くと、それぞれの状態をカードの中で表示してくれる  
UIの見た目を一覧確認しながらコードを修正できる

自分のコードは`reagent-material-ui`のまんまだから、あんまり利点はないかな

# e2e
`Taiko`という`node.js`で動く`appium`的なやつでテストが用意されている  
JavaScriptなのでClojureScriptからだとそのまま呼べる

# ユニットテストは？
`cljs.test`というのが組込みで用意されている  
テストの実行は、まずテストコードをJavaScriptにコンパイルする必要がある  
`$ shadow-cljs compile <プロジェクト構成名：test>`  
これで複数のcljsから１つのjsにバンドルした`test.js`が生成される  
これを`$ node test.js`とすることでテストが実行される  
Jestなんていらんかったんや  

テストコードは超シンプル
```clojure
(ns my-project.tests
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest test-numbers
  (is (= 1 1)))
```

# linterとかautofixとか
`clj-kondo`というlinterが`create-cljs-app`でデフォルトで入っている  
`zprint-clj`というautofixが`create-cljs-app`でデフォルトで入っている  
悩まなくていいのはうれしい

# EmacsからREPL繋ぐには
`M-x cider-jack-in-cljs`でREPLとWebサーバが起動する  
このWebサーバはwatchが有効なので、ホットリロードできる  
ホットリロードがあるから、あんまりREPLの活躍がない気がする  
もう少しユニットテストとかと組み合わせた開発者体験してみないとわからないか
