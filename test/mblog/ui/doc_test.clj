(ns mblog.ui.doc-test
  (:require [clojure.test :refer [deftest is]]
            [mblog.ui.doc :as ui.doc]))

(deftest doc->href
  (is (= (ui.doc/doc->href-FUTURE-AWESOME {:doc/slug "olorm-1"
                            :doc/cohort {:cohort/slug "olorm"}})
         "/olorm/olorm-1/")))
