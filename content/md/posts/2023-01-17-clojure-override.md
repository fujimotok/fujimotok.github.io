{:title "Clojure でライブラリの関数を差し替える（あるいはdefadvice）"
 :tags ["Clojure"]
 :layout :post
 :toc true}

Cryogenのsitemap.xmlがおかしかったので調査のために該当の関数を外から書き換えた。  
差し替えの方法を忘れないためにメモする。

## 関数を差し替えるとは
既存の処理が呼び出している関数を、自分の作った関数にすり替えを行う。  
Emacsだとdefadviceとかいって、既存関数の前後で呼ばれるようにしたり、関数を乗っ取ったりできる。  
こういうプロジェクト構成手法は「アスペクト指向プログラミング」と呼ばれるが、これプラスClojureで検索してもあんまり出てこなかった。

## やり方
`widh-redefs`を使う。  
差し替えを行う期間を`let`のフォームみたいにくくる。  
`let`の変数宣言のところに、置き換え対象シンボル名と置き換え後のシンボルのペアのリストを記述。
```clojure
(ns cryogen.core
  (:require
    [cryogen-core.sitemap :as sitemap])

(defn loc
  [^java.io.File f]
  (println "#####")
  (-> f (.getAbsolutePath) (.replace "\\" "/") (.split (cryogen-io/path cryogen-io/public "/")) second))

(defn compile-site
  []
  (with-redefs [sitemap/loc loc]
      (compile-assets-timed
        {:update-article-fn update-article
         :extend-params-fn extend-params})))
```

## sitemap.xmlがおかしかった原因
まず、Windows環境ではスラッシュとバックスラッシュが逆なので、そのあとのsplit()が意図通り動いていなかった。

次に、Linux環境であるGitHub Action上でもおかしかったのは、`config.edn`の設定に問題があった。  
`:site-url "http://example.com/"`の最後のスラッシュが必須なのを外していたのが悪かった。

Issues見る限りはWindows環境での不具合報告あって修正済みっぽかったが、治ってない。

## あとがき
とりあえず、GitHub Action環境で問題ないので、対策しない。  
ローカルで見る分にはsitemap.xmlは使わないので。  
これで、Google Search Consoleにも正しく読み込まれるようになった。

関数差し替えなど何でもありなところは動的言語のメリットだと感じた。
