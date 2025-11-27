(ns mblog.markdown-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [mblog.markdown :as markdown]))

(deftest parse
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
             (select-keys [:title :description]))))

  (testing "Rå HTML i Markdown-filer får flyte gjennom til HTML-en"
    (is (= (-> "
Morgenstund har gull i munn.

<div class=\"haha\">html inni!</div>
"
               markdown/parse
               :doc/hiccup)
         [:div
          [:p "Morgenstund har gull i munn."]
          [:div {:innerHTML "<div class=\"haha\">html inni!</div>"}]]))
    ))
