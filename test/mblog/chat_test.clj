(ns mblog.chat-test
  (:require [clojure.test :refer [deftest is testing]]
            [mblog.chat :as chat]))

(deftest innhold->hiccup
  (testing "renders fine with zero messages"
    (is (chat/innhold->hiccup nil)))

  (testing "message shows up in the dom"
    (is (contains? (->> [{:message/content "Is anybody still there?"}]
                        chat/innhold->hiccup
                        (tree-seq coll? identity)
                        set)
                   "Is anybody still there?"))))
