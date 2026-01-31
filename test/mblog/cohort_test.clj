(ns mblog.cohort-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mblog.cohort :as cohort]
   [mblog.testdb :as testdb]))

(deftest href-test
  (is (= "/urlog/"
         (cohort/href {:cohort/slug "urlog"})))
  (testing "Returnerer nil hvis vi ikke kjenner slug"
    (is (nil? (cohort/href {})))))

(deftest all-test
  (is (contains? (->> (cohort/all (testdb/get-instance))
                      (map :cohort/id)
                      (into #{}))
                 :cohort/olorm)))
