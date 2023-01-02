(ns cryogen.core
  (:require
    [cryogen-core.compiler :refer [compile-assets-timed]]
    [cryogen-core.plugins :refer [load-plugins]]
    [cryogen.addon.file-path :as file-path]
    [cryogen.addon.history :as history]
    [cryogen.addon.tag-count :as tag-count]
    [cryogen.addon.updated-at :as updated-at]))


(defn update-article
  [article params]
  (-> article
      (file-path/update-article params)
      (updated-at/update-article params)
      (history/update-article params)))


(defn extend-params
  [params site-data]
  (-> params
      (tag-count/extend-params site-data)))


(defn compile-site
  []
  (compile-assets-timed
    {:update-article-fn update-article
     :extend-params-fn extend-params}))


(defn -main
  []
  (load-plugins)
  (compile-site)
  (System/exit 0))
