(ns mblog.fjernkohortene
  (:require [babashka.fs :as fs]
            [babashka.http-client :as http-client]
            [clojure.edn :as edn]
            [clojure.pprint]
            [mblog.db :as db]
            [mblog.state :as state]))

(comment
  ;; Doc list, as communicated over HTTP endpoint:
  {:docs [{:doc/slug "enklere-1"}
          {:doc/slug "enklere-2"}
          {:doc/slug "enklere-3"}
          {:doc/slug "enklere-4"}
          {:doc/slug "enklere-5"}
          {:doc/slug "enklere-6"}
          {:doc/slug "enklere-7"}]}

  ;; In-memory, after docs have been read from Github:
  [{:slug "enklere-1"
    :markdown-str "# Mer kreativ med mer fokus?\n\n ..."
    :meta {:doc/created "2026-01-11", :doc/uuid "3e862723-f9f9-4cdf-be29-7913621d2b3a", :git.user/email "git@teod.eu"}}
   ,,,]

  :-)

(defn latest-sha [org repo branch]
  (-> (str "https://api.github.com/repos/" org "/" repo "/commits/" branch)
      (http-client/get {:headers {"Accept" "application/vnd.github.sha"}})
      :body))

(defn github-raw-href [rev path]
  (str "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/" rev "/" path))

(defn expect-str [maybe-str msg ex-data]
  (when-not (string? maybe-str)
    (throw (ex-info msg ex-data)))
  maybe-str)

(defn load-markdown-str [sha doc]
  (let [uri (github-raw-href sha (str "enklere/" (:doc/slug doc) "/index.md"))
        body (:body (http-client/get uri))]
    (expect-str body "Invalid markdown body" {:uri uri :body body})))

(def required-meta-keys #{:doc/created :doc/uuid :git.user/email})

(defn expect-meta [meta]
  (doseq [k required-meta-keys]
    (when-not (contains? meta k)
      (throw (ex-info "Invalid meta, missing key"
                      {:key k :meta meta})))
    (when-not (string? (get meta k))
      (throw (ex-info "Invalid meta value, must be string"
                      {:key k :value (get meta k) :meta meta}))))
  (into (sorted-map) (select-keys meta required-meta-keys)))

(defn load-meta [sha doc]
  (let [uri (github-raw-href sha (str "enklere/" (:doc/slug doc) "/meta.edn"))
        body (:body (http-client/get uri))
        meta (edn/read-string body)]
    (expect-meta meta)))

(defn load-all [{:keys [sha docs]}]
  (->> docs
       (pmap (fn [doc]
               {:slug (:doc/slug doc)
                :markdown-str (load-markdown-str sha doc)
                :meta (load-meta sha doc)}))
       (filter identity)))

(defn cohort-file [cohort & fs]
  (apply fs/file (System/getenv "GARDEN_STORAGE") (:cohort/root cohort) fs))

(defn pprint-str [x]
  (with-out-str (clojure.pprint/pprint x)))

(defn realize-doc [cohort slug markdown-str meta]
  (fs/create-dirs (cohort-file cohort slug))
  (spit (cohort-file cohort slug "meta.edn")
        (pprint-str meta))
  (spit (cohort-file cohort slug "index.md") markdown-str))

(def cohort-enklere {:cohort/root "enklere"})

(defn persist! [docs]
  (fs/create-dirs (cohort-file cohort-enklere))
  (doseq [{:keys [slug markdown-str meta]} docs]
    (realize-doc cohort-enklere slug markdown-str meta))
  :done)

(defonce !last-req (atom nil))

(defn reload-hook [req]
  (reset! !last-req req)
  (future
    (println "Persisting new docs to disk ...")
    (-> req :body slurp edn/read-string
        (select-keys [:docs])
        (assoc :sha (latest-sha "mikrobloggeriet" "fjernkohortene" "master"))
        load-all persist!)
    (println "Loading new docs to database ...")
    (alter-var-root #'mblog.state/datomic (fn [db] (db/add-docs db (db/load-docs db))))
    (println "New docs loaded."))
  {:status 202})
