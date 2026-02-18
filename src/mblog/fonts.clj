(ns mblog.fonts
  (:require
   [babashka.fs :as fs]))

(defn filenames [path]
  (->> (fs/list-dir path)
       (map fs/file-name)
       (map str)
       sort
       vec))

(def fonts (filenames "public/css/fonts"))

(defn random []
  (rand-nth fonts))

