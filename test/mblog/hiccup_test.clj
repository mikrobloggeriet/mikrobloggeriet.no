(ns mblog.hiccup-test
  (:require [clojure.test :refer [deftest is testing]]
            [mblog.hiccup :as hiccup]))

(deftest lazyload
  (is (= [:div [:img {:loading "lazy"}]]
         (hiccup/transform :img hiccup/lazyload [:div [:img]])))

  (is (= [:div [:img {:loading "lazy"} "body"]]
         (hiccup/transform :img hiccup/lazyload [:div [:img "body"]])))

  (testing "can also do other tags"
    (is (= [:div [:iframe {:loading "lazy" :class "lol"} "body"]]
           (hiccup/transform :iframe hiccup/lazyload [:div [:iframe {:class "lol"} "body"]])))))
