(ns mblog.mime
  (:require
   [babashka.fs :as fs]
   [ring.util.mime-type]))

(def mime-types
  (-> ring.util.mime-type/default-mime-types
      (assoc "js" "text/javascript; charset=utf-8")
      (assoc "mjs" "text/javascript; charset=utf-8")))

(defn file->type [f]
  (get mime-types (fs/extension f) "application/octet-stream"))
