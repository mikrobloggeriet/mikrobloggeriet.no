(ns mblog.theme-test
  (:require
   [mblog.theme :as theme]
   [clojure.test :refer [deftest testing is]]))

(def red [255 0 0])

(def blue [0 0 255])

(deftest create-overrides
  (is (= (theme/create-overrides {:bg-color {:value red
                                             :locked? true}})
         {}))
  (is (= (keys (theme/create-overrides {:bg-color {:value red
                                                   :locked? false}}))
         [:bg-color])))

(deftest update-and-get*
  (testing "New users get a random theme"
    (is (contains? (theme/update-and-get* (atom {"TEODOR" {}}) "TEODOR")
                   :bg-color)))

  (testing "Locked themes persist"
    (is (= (theme/update-and-get* (atom {"OLAV" {:bg-color {:value blue :locked? true}}})
                                  "OLAV")
           {:bg-color {:value blue :locked? true}})))

  (testing "Bullshit, non-locked values change"
    (is (not= (get-in (theme/update-and-get* (atom {"OLAV" {:bg-color {:value "BULLSHIT" :locked? false}}})
                                             "OLAV")
                      [:bg-color :value])
              "BULLSHIT")))

  (testing "Store gets updated"
    (let [store (atom {})]
      (theme/update-and-get* store "TEODOR")
      (is (contains? @store "TEODOR")))))
