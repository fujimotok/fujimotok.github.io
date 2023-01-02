;; file-pathを追加
;; update_at.clj, history.cljで使用


(ns cryogen.addon.file-path
  (:require
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


(defn update-article
  [article _]
  (let [file-name ; file-nameがhtmlなったものが来るのでmdに変換
        (str/replace (:file-name article) #"\.html" ".md")
        path (filename2path file-name)]
    (merge
      article
      {:file-path path})))


