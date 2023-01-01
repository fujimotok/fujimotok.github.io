;; Gitから更新日時を取得しarticleに追加
;; postページで使用
;; theme/<theme-name>/html/post-content.html
;; -<strong>{{post.date|date:longdDarte}}</strong>
;; +<strong>📆 Published: {{post.date|date:"yyyy-MM-dd"}} / 📆 Last updated: {{post.update|date:"yyyy-MM-dd"}}</strong>


(ns cryogen.addon.updated-at
  (:require
    [clj-jgit.porcelain :as git]
    [clojure.java.io :as io]
    [clojure.string :as str]))


(defn- filename2path
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


(defn- get-file-update-on
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


