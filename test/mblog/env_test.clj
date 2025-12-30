(ns mblog.env-test
  (:require [clojure.test :refer [deftest is]]
            [mblog.env :as env]))

(deftest env*
  (is (= :env/dev (env/env* "http://localhost:777")))
  (is (= :env/prod (env/env* "https://mikrobloggeriet.no")))
  (is (= :env/unknown (env/env* nil))))
