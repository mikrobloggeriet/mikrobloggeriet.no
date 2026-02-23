(ns mblog.command-test
  (:require
   [clojure.test :refer [deftest is]]
   [mblog.command :as command]))

(deftest post
  (is (= (command/post :command.theme/lock-text-color)
         "@post('/command/theme/lock-text-color')")))

(deftest from-request
  (is (= (command/from-request
          {:reitit.core/match
           {:path-params
            {:group "theme"
             :slug "lock-text-color"}}})
         {:group "theme"
          :slug "lock-text-color"})))
