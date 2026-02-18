(ns mblog.theme-test
  (:require
   [mblog.theme :as theme]
   [clojure.test :refer [deftest testing is]]))

(def white [255 255 255])
(def black [0 0 0])

(deftest generate-colors
  (is (= (theme/generate-colors {:bg-color black :text-color white})
         {:bg-color black :text-color white}))

  (is (contains? (theme/generate-colors {:bg-color white})
                 :text-color))
  (is (contains? (theme/generate-colors {:text-color black})
                 :bg-color)))

(deftest create-overrides
  (is (= (theme/create-overrides {:bg-color {:value black
                                             :locked? true}})
         {}))
  (is (= (keys (theme/create-overrides {:bg-color {:value white
                                                   :locked? false}}))
         [:bg-color])))

(deftest update-and-get*
  (testing "New users get a random theme"
    (is (contains? (theme/update-and-get* (atom {"TEODOR" {}}) "TEODOR")
                   :bg-color)))

  (testing "Locked themes persist"
    (is (= (theme/update-and-get* (atom {"OLAV" {:bg-color {:value black :locked? true}}})
                                  "OLAV")
           {:bg-color {:value black :locked? true}})))

  (testing "Bullshit, non-locked values change"
    (is (not= (get-in (theme/update-and-get* (atom {"OLAV" {:bg-color {:value "BULLSHIT" :locked? false}}})
                                             "OLAV")
                      [:bg-color :value])
              "BULLSHIT")))

  (testing "Store gets updated"
    (let [store (atom {})]
      (theme/update-and-get* store "TEODOR")
      (is (contains? @store "TEODOR")))))
