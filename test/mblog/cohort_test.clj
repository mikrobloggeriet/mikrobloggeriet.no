(ns mblog.cohort-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mblog.cohort :as cohort]
   [mblog.db :as db]))

(deftest href-test
  (is (= "/urlog/"
         (cohort/href {:cohort/slug "urlog"})))
  (testing "Returnerer nil hvis vi ikke kjenner slug"
    (is (nil? (cohort/href {})))))

(def db (db/loaddb {:cohorts db/cohorts :authors db/authors}))

(deftest all-test
  (is (contains? (->> (cohort/all db)
                      (map :cohort/id)
                      (into #{}))
                 :cohort/olorm)))
