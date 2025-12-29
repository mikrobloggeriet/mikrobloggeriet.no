(ns mblog.leik
  "en ekstra leken kohort"
  (:require
   [babashka.fs :as fs]
   [mblog.git :as git]))

(defn load-doc [f]
  (when (fs/exists? f)
    (when-let [number (second (re-matches #"(\d+)\.md" (fs/file-name f)))]
      {:doc/slug (str "leik-" number)
       :doc/markdown (slurp (fs/file f))
       :doc/created (git/created f)
       :doc/cohort [:cohort/slug "leik"]})))

(defn find-docs []
  (->> (fs/list-dir "text/leik")
       (keep load-doc)))

(comment
  (do (set! *print-namespace-maps* false)
      (def f "text/leik/1.md")
      (require '[mblog.doc]))

  (->> (find-docs)
       (map mblog.doc/select-meta)
       (take 4))

  (fs/file-name f)
  (-> (load-doc f) (dissoc :doc/markdown))

  )
