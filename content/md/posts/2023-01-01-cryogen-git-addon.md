{:title "Cryogen でページの更新日を git からとってくる拡張を作った"
 :tags  ["Cryogen" "Clojure"]
 :layout :post
 :toc true}

Cryogenは投稿日を表示する機能があるが、後から変更したときの更新日も欲しい。  
更新日を手で入力なんてしてられないので、Gitから取ってきてほしい。  
Clojureで拡張機能を書けるので、練習がてら作った。

## 作った背景
[前の投稿](https://fujimotok.github.io/posts/2022-12-28-cryogen-101/#機能拡張)で触れたとおり、Clojreで機能拡張を書ける。

Cryogenのデフォルトでは、ファイル名の先頭に付けた日付が記事の先頭に付く。

自分の使い方的に、Wikiのようにどんどん更新したいので、更新日も一緒に付けるようしたい。

## 実現手段
`update-article-fn`をフックする。  
こいつの中で`article`に情報を追加して返すと、  
テンプレートの中で`{{NAME}}`として参照できる。

今回は`update`という情報を追加して、`themes/<テーマ名/post-content.html`で投稿日の横に表示した。

`update`には、`git log`をファイル指定で実行し、最新だけをとってきて、その中に入っている日付を入れるようにした。

ClojureからGit情報にアクセスするのには[clj-jgit](https://github.com/clj-jgit/clj-jgit)を使った。  
シェルスクリプトがGitHub Actionsで動くかわからないし、自分のマシンがWindowsなので。

## コード
```clojure
(ns cryogen.core
  (:require
    [clj-jgit.porcelain :as git]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [cryogen-core.compiler :refer [compile-assets-timed]]
    [cryogen-core.plugins :refer [load-plugins]]))


(defn filename2path
  [file-name]
  (let [files ; file-nameからパスを得るためにfileオブジェクトを検索
        (filter #(= file-name (.getName %))
                (file-seq (io/file ".")))]
    (-> files
        ;; 1個でもコレクションになってるので単品を取り出し
        first
        .toPath
        ;; PATHオブジェクトが返ってくるのでstringに
        .toString
        ;; cjl-jgitがスラッシュ区切りにしか対応していない
        ;; Windows環境ではバックスラッシュが返ってくるので置換
        (str/replace #"\\" "/")
        ;; cjl-jgitが./からのパスだとうまく動かないので置換
        (str/replace #"\./" ""))))


(defn get-file-update-on
  [path]
  (-> (git/load-repo ".")
      (git/git-log :max-count 1 :paths path)
      first
      :committer
      :date))


(defn update-article
  [article _]
  (let [file-name ; file-nameがhtmlなったものが来るのでmdに変換
        (str/replace (:file-name article) #"\.html" ".md")
        path (filename2path file-name)
        date (get-file-update-on path)]
    (merge
      article
      {:update date})))


(defn compile-site
  []
  (compile-assets-timed
    {:update-article-fn update-article}))


(defn -main
  []
  (load-plugins)
  (compile-site)
  (System/exit 0))
```

## あとがき
これ、拡張が増えてきたらどう管理するのがいいんだろうか？

拡張毎にファイル分けて、エントリポイントだけ出して、`core.clj`がフック関数に羅列していくのがいいかな。
```clojure
(ns cryogen.core
  (:require
    [cryogen.update-on :as update-on]]
    [cryogen-core.compiler :refer [compile-assets-timed]]
    [cryogen-core.plugins :refer [load-plugins]]))

(defn update-article
  [article params]
  (-> article
      (update-on/update-article params)))

(defn compile-site
  []
  (compile-assets-timed
    {:update-article-fn update-article}))

(defn -main
  []
  (load-plugins)
  (compile-site)
  (System/exit 0))
```
