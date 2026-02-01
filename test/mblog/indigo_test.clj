(ns mblog.indigo-test
  (:require
   [clojure.test :refer [deftest is]]
   [mblog.indigo :as indigo]
   [mblog.samvirk :as samvirk]
   [mblog.testdb :as testdb]))

(deftest left-bar
  (is
   (contains?
    (->> {:docs [{:doc/title "Unminifying av kode med LLM"
                  :doc/markdown "lang tekst"}]
          :samvirk (samvirk/load)
          :motto "Alt for Norge!"}
         (indigo/innhold->hiccup)
         (tree-seq seqable?
                   identity)
         set)
    "Unminifying av kode med LLM")))

(deftest req->innhold
  (let [innhold (indigo/req->innhold {:system/datomic (testdb/get-instance)})
        docs (:docs innhold)
        slugs (->> docs
                   (map :doc/slug)
                   (into (sorted-set)))]
    (is (contains? slugs "olorm-1"))
    (is (contains? slugs "jals-2"))
    (is (nil? (:current-cohort docs)))))
