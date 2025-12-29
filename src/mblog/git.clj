(ns mblog.git
  (:require
   [babashka.fs :as fs]
   [babashka.process :as p]
   [clojure.string :as str]))

(defn created [f]
  (->> f
       (p/shell {:out :string} "git log --diff-filter=A --follow --format=%aI --")
       :out str/trim))

(comment
  (def f "text/leik/1.md")

  (str (fs/creation-time f))
  ;; => "2025-11-26T18:49:07Z"

  p/process

  (-> (p/shell {:out :string}
               "git log --diff-filter=A --follow --format=%aI -- text/leik/11.md")
      :out str/trim)
  ;; => "2025-09-22T09:03:05+02:00"

  ;; => "2025-03-19T17:43:03+01:00"

  )
