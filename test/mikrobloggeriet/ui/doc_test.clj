(ns mikrobloggeriet.ui.doc-test
  (:require [clojure.test :refer [deftest is]]
            [datomic.api :as d]
            [mblog.testdb :as testdb]
            [mikrobloggeriet.ui.doc :as ui.doc]))

(deftest page-test
  (let [db (testdb/get-instance)
        oj-2 (d/entity db [:doc/slug "oj-2"])]
    (is (map? (ui.doc/page oj-2 {})))))

