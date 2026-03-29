(ns mblog.smoke-test
  (:require
   [clojure.test :refer [deftest is]]
   [mblog.serve :as serve]
   [mblog.testdb :as testdb]
   [mblog.page-registry :as page-registry]))

(deftest ring-handler-returns-olorm-1
  (let [response ((serve/create-ring-handler)
                  {:uri "/doc/olorm-1"
                   :request-method :get
                   :system/datomic (testdb/get-instance)
                   :system/page-registry page-registry/registry})]
    (is (= 200 (:status response)))
    (is (some? (:body response)))))
