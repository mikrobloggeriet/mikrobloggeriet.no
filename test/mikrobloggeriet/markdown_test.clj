(ns mikrobloggeriet.markdown-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [mikrobloggeriet.markdown :as markdown]))

(comment
  (set! *print-namespace-maps* false)
  )

(deftest parse-markdown2*
  (is (= {:doc/html "<div><p>tekst</p></div>"
          :doc/hiccup [:div [:p "tekst"]]
          :title nil
          :description "tekst"}
         (markdown/parse-markdown "tekst")))

  (is (= {:title "Håvamål"
          :description "Augo du bruke fyrr inn du gjeng,"}
         (-> "
# Håvamål

Augo du bruke fyrr inn du gjeng,
"
             str/trim
             markdown/parse-markdown
             (select-keys [:title :description])))))
