(ns mblog.markdown-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [mblog.markdown :as markdown]))

(deftest parse-markdown2*
  (is (= {:doc/html "<div><p>tekst</p></div>"
          :doc/hiccup [:div [:p "tekst"]]
          :title nil
          :description "tekst"}
         (markdown/parse "tekst")))

  (is (= {:title "Håvamål"
          :description "Augo du bruke fyrr inn du gjeng,"}
         (-> "
# Håvamål

Augo du bruke fyrr inn du gjeng,
"
             str/trim
             markdown/parse
             (select-keys [:title :description])))))
