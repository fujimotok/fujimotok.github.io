(ns cryogen.core
  (:require
    [cryogen-core.compiler :refer [compile-assets-timed]]
    [cryogen-core.plugins :refer [load-plugins]]
    [cryogen.addon.updated-at :as updated-at]))


(defn update-article
  [article params]
  (-> article
      (updated-at/update-article params)))


(defn compile-site
  []
  (compile-assets-timed
    {:update-article-fn update-article}))


(defn -main
  []
  (load-plugins)
  (compile-site)
  (System/exit 0))
