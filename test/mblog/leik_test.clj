(ns mblog.leik-test
  (:require [clojure.test :refer [deftest is]]
            [mblog.leik :as leik]))

(def leik-1 (leik/load-doc "text/leik/1.md"))

(deftest lager-riktig-slug
  (is (= "leik-1"
         (:doc/slug leik-1))))
