(ns mblog.smoke-test
  (:require
   [clojure.test :refer [deftest is]]
   [mblog.serve :as serve]
   [mblog.testdb :as testdb]))

(deftest ring-handler-returns-olorm-1
  (let [response ((serve/create-ring-handler)
                  {:uri "/doc/olorm-1"
                   :request-method :get
                   :system/datomic (testdb/get-instance)})]
    (is (= 200 (:status response)))
    (is (some? (:body response)))))
