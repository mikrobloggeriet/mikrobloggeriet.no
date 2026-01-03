(ns dev
  (:require [clojure.java.browse]))

(def browse! #(clojure.java.browse/browse-url "http://localhost:7777"))

(comment ;; s-:
  (browse!)

  )
