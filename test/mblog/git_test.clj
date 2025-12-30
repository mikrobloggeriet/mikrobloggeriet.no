(ns mblog.git-test
  (:require [clojure.test :refer [deftest is]]
            [mblog.env]
            [mblog.git :as git]))

(when-not (mblog.env/unknown?)
  (deftest created-date
    (is (= "2023-03-28T20:57:54+02:00"
           (git/created "README.md")))))
