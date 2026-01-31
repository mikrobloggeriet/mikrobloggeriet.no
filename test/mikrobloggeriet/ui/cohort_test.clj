(ns mikrobloggeriet.ui.cohort-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [datomic.api :as d]
            [mblog.testdb :as testdb]
            [mikrobloggeriet.ui.cohort :as ui.cohort]))

(deftest doc-table-test
  (let [db (testdb/get-instance)
        olorm (d/entity db [:cohort/id :cohort/olorm])
        response (ui.cohort/doc-table db olorm {})]
    (is (map? response))
    (let [{:keys [body]} response]
      (is (str/includes? body "Alle OLORM-er"))
      (is (str/includes? body "publisert"))
      (is (str/includes? body "olorm-13")))))
