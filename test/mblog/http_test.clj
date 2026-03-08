(ns mblog.http-test
  (:require [mblog.http :as http]
            [clojure.test :refer [deftest is]]))

(deftest parse-session
  (is (= {} (http/parse-session {})))
  (is (= "abc123"
         (-> {:headers {"cookie" "session_id=abc123"}}
             http/parse-session :session/id))))
