(ns mblog.xlink
  "utilities for cross linking")

;; Don't call into pages from this.
;; Pages use xlink, not the other way around.

(defn doc [doc]
  (str "/doc/" (:doc/slug doc)))

(defn les [doc]
  (str "/les/" (:doc/slug doc)))
