(ns cryogen.addon.calendar
  (:import
    (java.text
      SimpleDateFormat)
    (java.util
      Calendar)))


(defn- get-uri-where-date
  [posts date]
  (let [df (new SimpleDateFormat "yyyy-MM-dd")]
    (->> posts
         (filter #(= (.format df (:date %))
                     (.format df date)))
         first
         :uri)))


(defn- create-item
  [site-data calendar]
  {:date (.get calendar Calendar/DAY_OF_MONTH)
   :uri (get-uri-where-date (:posts site-data) (.getTime calendar))})


(defn- create-items
  [site-data calendar]
  ;; ４週間分のitemを生成して１週間ごとに分ける
  (->> (range (* 7 4))
       (map (fn [_]
              ;; 副作用のないaddがないので、仕方なく日付をインクリメントする
              (.add calendar Calendar/DAY_OF_MONTH 1)
              (create-item site-data calendar)))
       (partition 7)))


(defn extend-params
  [params site-data]
  (let [cal (Calendar/getInstance)
        day-of-week (.get cal Calendar/DAY_OF_WEEK)
        month (inc (.get cal Calendar/MONTH))
        year (.get cal Calendar/YEAR)]
    ;; calを３週間前の日曜日の日付にする
    (.add cal Calendar/DAY_OF_MONTH (- (+ day-of-week 21)))
    (merge params
           {:calendar
            {:month (format "%d/%02d" year month),
             :items (create-items site-data cal)}})))

