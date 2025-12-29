(ns mblog.git-test
  (:require [clojure.test :refer [deftest is]]
            [mblog.git :as git]))

(deftest created-date
  (is (= "2023-03-28T20:57:54+02:00"
         (git/created "README.md"))))
