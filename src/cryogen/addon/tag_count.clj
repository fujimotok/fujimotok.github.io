;; タグの出現回数をパラメータに追加
;; タグページで使用
;; theme/<theme-name>/html/tags.html
;; - <li><a href="{{tag.uri}}">{{tag.name}}</a></li>
;; + <li><a href="{{tag.uri}}">{{tag.name}}</a> ({{tag.count}})</li>


(ns cryogen.addon.tag-count)


(defn- get-tag-counts
  [site-data]
  (->> (:posts-by-tag site-data)
       ;; (タグ名 . 記事の配列) -> (タグ名 . カウント)
       (map (fn [[k v]] [k (count v)]))
       ;; vector -> map
       (into {})))


(defn- update-tags
  [tags tag-counts]
  ;; tags = [{:name "name", :uri "uri"} ...]
  ;; tag-counts = {:<tag-name> count ...}
  (map (fn [t]
         ;; tag に新しく :count フィールドを追加
         (assoc t :count
                ;; タグ名から数を引いてくる
                (tag-counts (:name t))))
       tags))


(defn extend-params
  [params site-data]
  (let [tag-counts (get-tag-counts site-data)]
    (update
      params :tags
      #(update-tags % tag-counts))))
