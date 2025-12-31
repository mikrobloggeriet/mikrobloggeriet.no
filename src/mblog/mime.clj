(ns mblog.mime
  (:require
   [ring.util.mime-type]))

(def mime-types
  (-> ring.util.mime-type/default-mime-types
      (assoc "js" "text/javascript; charset=utf-8")
      (assoc "mjs" "text/javascript; charset=utf-8")))
