(ns mblog.db-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [datomic.api :as d]
   [mblog.db :as db]
   [mblog.testdb :as testdb]))

(deftest valid-cohort-data
  (doseq [cohort (vals db/cohorts)]
    (is (contains? cohort :cohort/name))
    (is (contains? cohort :cohort/description))
    (is (contains? cohort :cohort/root))))

(deftest doc-test
  (testing "Docs have cohorts"
    (let [db (testdb/get-instance)]
      (testing "olorm funker fjell"
        (is (= (d/entity db [:cohort/id :cohort/olorm])
               (:doc/cohort (d/entity db [:doc/slug "olorm-12"])))))

      (testing "leik har også kohort!"
        (is (some? (:doc/cohort (d/entity db [:doc/slug "leik-3"]))))))))
