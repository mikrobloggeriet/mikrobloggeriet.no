(ns mblog.fjernkohortene
  (:refer-clojure :exclude [load])
  (:require [babashka.fs :as fs]
            [babashka.http-client :as http-client]
            [clojure.edn :as edn]
            [clojure.pprint]))

(comment
  ;; Remote data structure, from fjernkohortene:
  {:docs [{:doc/slug "enklere-1"}
          {:doc/slug "enklere-2"}
          {:doc/slug "enklere-3"}
          {:doc/slug "enklere-4"}
          {:doc/slug "enklere-5"}
          {:doc/slug "enklere-6"}
          {:doc/slug "enklere-7"}]}

  :-)

(defonce !last-req (atom nil))

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

(comment
  ;; In-memory, loaded
  [{:slug "enklere-1"
    :markdown-str "# Mer kreativ med mer fokus?\n\n ..."
    :meta {:doc/created "2026-01-11", :doc/uuid "3e862723-f9f9-4cdf-be29-7913621d2b3a", :git.user/email "git@teod.eu"}}
   ,,,]

  :-)

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
  (prn :load-all)
  (->> docs
       (pmap (fn [doc]
               {:slug (:doc/slug doc)
                :markdown-str (load-markdown-str sha doc)
                :meta (load-meta sha doc)}))
       (filter identity)))

(defn cohort-file [cohort & fs]
  (apply fs/file
         (System/getenv "GARDEN_STORAGE")
         (:cohort/root cohort)
         fs))

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

(defn reload-hook [req]
  (prn :request)
  (reset! !last-req req)
  (future (-> req :body slurp edn/read-string
              (select-keys [:docs])
              (assoc :sha (latest-sha "mikrobloggeriet" "fjernkohortene" "master"))
              load-all persist!))
  {:status 202})

(comment
  ;; Filstruktur hos Mikrobloggeriet:
  ;;
  ;; enklere/
  ;;   cohort.edn
  ;;   enklere-1/index.md
  ;;   enklere-1/meta.edn
  ;;   enklere-2/index.md
  ;;   enklere-2/meta.edn

  ;; Kopiert
  (def manifest
    '{:cohorts
      [{:cohort/id :cohort/enklere,
        :cohort/root "enklere",
        :cohort/slug "enklere",
        :cohort/type :cohort.type/markdown,
        :cohort/name "ENKLERE",
        :cohort/description "Kan enklere være bedre?"}],
      :docs
      ({:doc/cohort [:cohort/id :cohort/enklere],
        :slug "enklere-1",
        :md
        {:href
         "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/8fc2de2c6e85a3bc57570f0d7df0720ca2039bd7/enklere/enklere-1/index.md"},
        :meta
        {:href
         "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/8fc2de2c6e85a3bc57570f0d7df0720ca2039bd7/enklere/enklere-1/meta.edn"}}
       {:doc/cohort [:cohort/id :cohort/enklere],
        :slug "enklere-3",
        :md
        {:href
         "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/8fc2de2c6e85a3bc57570f0d7df0720ca2039bd7/enklere/enklere-3/index.md"},
        :meta
        {:href
         "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/8fc2de2c6e85a3bc57570f0d7df0720ca2039bd7/enklere/enklere-3/meta.edn"}}
       {:doc/cohort [:cohort/id :cohort/enklere],
        :slug "enklere-2",
        :md
        {:href
         "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/8fc2de2c6e85a3bc57570f0d7df0720ca2039bd7/enklere/enklere-2/index.md"},
        :meta
        {:href
         "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/8fc2de2c6e85a3bc57570f0d7df0720ca2039bd7/enklere/enklere-2/meta.edn"}}),
      :rev "8fc2de2c6e85a3bc57570f0d7df0720ca2039bd7"})

  (realize-manifest manifest)

  (def enklere (-> manifest :cohorts first))
  (def id->cohort (into {} (map (juxt :cohort/id identity)) (:cohorts manifest)))

  )
