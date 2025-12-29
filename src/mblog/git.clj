(ns mblog.git
  (:require
   [babashka.process :as p]
   [clojure.string :as str]))

(defn created [f]
  (->> f
       (p/shell {:out :string} "git log --diff-filter=A --follow --format=%aI --")
       :out str/trim))

(comment
  (created "text/leik/11.md")
  ;; => "2025-09-22T09:03:05+02:00"

  (created "README.md")
  ;; => "2023-03-28T20:57:54+02:00"

  )
