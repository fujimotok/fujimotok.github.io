;; Gitから更新日時を取得しarticleに追加
;; postページで使用
;; theme/<theme-name>/html/post-content.html
;; -<strong>{{post.date|date:longdDarte}}</strong>
;; +<strong>📆 Published: {{post.date|date:"yyyy-MM-dd"}} / 📆 Last updated: {{post.update|date:"yyyy-MM-dd"}}</strong>


(ns cryogen.addon.updated-at
  (:require
    [clj-jgit.porcelain :as git]))


(defn- get-file-update-on
  [path]
  (-> (git/load-repo ".")
      (git/git-log :max-count 1 :paths path)
      first
      :committer
      :date))


(defn update-article
  [article _]
  (let [date (get-file-update-on (:file-path article))]
    (merge
      article
      {:update date})))


