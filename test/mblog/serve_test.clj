(ns mblog.serve-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [clojure.walk :refer [prewalk]]
   [mblog.db :as db]
   [mblog.testdb :as testdb]
   [mblog.serve :as serve]
   [reitit.core]
   [reitit.ring]))

(deftest index-test
  (let [db (testdb/get-instance)
        index-resp (serve/index {:system/datomic db})
        index (:body index-resp)]
    (testing "An index was returned"
      (is (some? index)))

    (testing "Index looks like html"
      (is (and (string? index)
               (str/starts-with? index "<!DOCTYPE"))))

    (testing "Index refers to olorm-4"
      (is (str/includes? index "/doc/olorm-4")))))

(deftest doc-test
  (let [db (testdb/get-instance)
        ring-handler (serve/create-ring-handler)
        injected-app (fn [req]
                       (ring-handler (assoc req :system/datomic db)))]
    ;; Sanity test that one document for each cohort renders successfully. Makes
    ;; it more comfortable to work with doc logic!
    (let [olorm-1 (injected-app {:uri "/doc/olorm-1" :request-method :get})]
      (is (str/includes? (str/lower-case (:body  olorm-1))
                         "søvn")
          "OLORM-1 handler om viktigheten av en god natts søvn."))

    (let [jals-1 (injected-app {:uri "/doc/jals-1" :request-method :get})]
      (is (str/includes? (str/lower-case (:body jals-1))
                         "modeller")
          "JALS-1 handler maskinlæringsmodeller."))

    (let [oj-1 (injected-app {:uri "/doc/oj-1" :request-method :get})]
      (is (str/includes? (str/lower-case (:body oj-1))
                         "refaktorering")
          "OJ-1 handler refaktorering."))))

(defn strip-function-objects
  "Remove function objects from a data structure so that the rest can be compared
  with another using clojure.core/= in tests"
  [x]
  (prewalk (fn [node]
             (if (fn? node)
               nil
               node))
           x))

(deftest markdown-cohort-routes-test
  (is (= ["/olorm"
          ["/" {:get nil, :name :mikrobloggeriet.olorm/all}]
          ["/:slug/" {:get nil, :name :mikrobloggeriet.olorm/doc}]]
         (-> (serve/markdown-cohort-routes (:cohort/olorm db/cohorts))
             strip-function-objects))))

(deftest cohorts-are-named
  (is (= :mikrobloggeriet.olorm/all
         (-> (reitit.core/match-by-path (reitit.ring/get-router serve/router)
                                        "/olorm/")
             :data :name))))

(deftest docs-are-named
  (is (= :mikrobloggeriet.olorm/doc
         (-> (reitit.core/match-by-path (reitit.ring/get-router serve/router)
                                        "/olorm/olorm-1/")
             :data :name))))
