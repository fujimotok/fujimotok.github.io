;; Gitから更新日時を取得しarticleに追加
;; postページで使用
;; theme/<theme-name>/html/post-content.html



(ns cryogen.addon.history)


(defn update-article
  [article _]
  (let [root "https://github.com/fujimotok/fujimotok.github.io/commits/main/"
        url (str root (:file-path article))]
    (merge
      article
      {:history url})))
