{:title "Clojure のモヤるところ"
 :tags ["Clojure"]
 :layout :post
 :toc true}

Clojureでいつも悩むところを書き出して、対策を調べておく。

## -> と ->>
どっちが第一引数に入れるやつで、どっちが最終引数に入れるやつかを忘れる。  
しかもGoogleで検索しにくい。

-> は`thread first`マクロ。  
->> は`thread last`マクロと呼ぶらしい。

1は前、2は後ろと憶えるか。

あと、これに渡すフォームたちが引数の位置が全部そろってないと使えなくなるのもモヤる。

シーケンスを受け取る関数は、基本は後ろになってるっぽい。
- `(map f coll)`
- `(filter f coll)`
- `(reduce f coll)`
- `(first coll)`
- `(every? pred coll)`

マップを受け取る関数は、基本は前になってるっぽい。
- `(:key map)` valが返ってくる
- `(find map key)` keyとvalが返ってくる
- `(assoc map key val)` 追加or上書きする

threadマクロがキマると、コードがすっきりして気持ちいい。

## map関数とmap構造
単純に検索しずらい。

## vectorとlistの違い
どっちも`map`とか使えるし違いが分からない。  
->[ここ](https://japan-clojurians.github.io/clojure-site-ja/guides/learn/sequential_colls)読んだら理解できた。

`vector`はインデックスでアクセス可能で、後ろにデータを追加する。  
`list`はインデックスでアクセス不可能で、前にデータを追加する。  
実際には`(nth coll index)`があるからインデックスでアクセスは可能。  
`vector`が`(get coll index)`でも取得できる点が違うくらいか。  
データ構造の性質やアクセス時間のオーダーは一般の配列、リンクリストの考えと同じ。

`vector`や`map`に専用のカッコを割り振ってるのは、最初はLispのシンタックス簡潔さから離れて嫌に思えたが、今ではデータと式との区別になって良いと思えるようになった。

## 文字列のjoin()はstringをrequireしないといけない
JavaScriptだとデフォルトで使えるが、Clojureではrequireが必要。  
一応`printf`相当の`(format fmt &args)`や、結合`(str &args)`はrequire不要。

+演算子のオーバーライドがないのは、他言語から来た人には引っかかるだろう。  
シンプル化という意味では`str`のコンストラクタ相当に1本化してるのは良い。  
演算子オーバーライドはあんまり良く思われてないし。

## letの変数定義部分が長くなる
関数の引数にさらに関数を入れると長くなる。  
対策として一時変数を導入する。  
メリットとして、戻り値に名前がついて見通しがよくなる。  
しかし、letの変数定義部が長くなる。

JavaScriptとかだど、使う直前で定義して見やすくなるが、  
Clojureにはletでしか変数作れないので、絶対に定義と使う場所が離れてしまう。

letの中でさらにlet使えばいいか。  
名前を付けたいだけなら使う周辺でletすればいける。

## 分配束縛
いつも忘れる。
```Clojure
(let [{:keys [a b]} {:a 1 :b 2 :c 3}]
     (println a b))
```

関数の引数に使う場合が多い。
```Clojure
(defn fun1
      [{:keys [a b]}]
      (println a b))
```

波カッコ、:keys、キーからコロンとったモノのvectorで構成される。

## データ構造
いきなりキーでアクセスしてるのだけがソースに書いてある。  
入力に型がないから、元がどのようなデータ構造なのか、情報がない。  
動かしてみないとわからない。

逆に言うと、どんなデータ構造が来ても、とりあえずそのキーがある時には処理するよってなっている。

"It is better to have 100 functions operate on one data structure than to have 10 functions operate on 10 data structures."  
「10種類のデータ構造にそれぞれを扱う10個の関数があるよりも、ひとつのデータ構造を扱う100個の関数がある方が良い。」

↑の思想はこうやって実現するわけか。

入力チェックが気になるなら、Specを使えばいいのかな？

## あとがき
Clojureで書くと、パズルを解くみたいに、短く簡潔に書けるのがうれしい。  
が、自分の力ではまだまだ上手く書けない。

もっと他人の書いたClojureコードを読まないといけないな。
