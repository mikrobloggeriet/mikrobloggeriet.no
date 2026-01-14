(ns mblog.fjernkohortene
  (:require [babashka.fs :as fs]))

(defn cohort-file [cohort & fs]
  (apply fs/file
         (System/getenv "GARDEN_STORAGE")
         (:cohort/root cohort)
         fs))

(defn realize-cohort [cohort]
  (fs/create-dirs (cohort-file cohort))
  (spit (cohort-file cohort "cohort.edn") (pr-str (dissoc cohort :cohort/root))))

(defn realize-doc [{:as doc :keys [slug md meta]} cohort]
  (fs/create-dirs (cohort-file cohort slug))
  (spit (cohort-file cohort slug "meta.edn") (slurp (:href meta)))
  (spit (cohort-file cohort slug "index.md") (slurp (:href md))))

(defn realize-manifest [{:as manifest :keys [cohorts docs]}]
  (doseq [cohort cohorts]
    (realize-cohort cohort))
  (let [id->cohort (into {} (map (juxt :cohort/id identity)) cohorts)]
    (doseq [doc docs]
      (let [cohort (id->cohort (-> doc :doc/cohort second))]
        (realize-doc doc cohort)))))

(comment
  (mapv str (fs/glob (cohort-file enklere) "**/*"))
  (fs/list-dir (cohort-file enklere))
  (fs/delete-tree (cohort-file enklere))
  (realize-manifest manifest)
  )

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
        :md {:href "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/5d9c9172f29fa62f6d5c20177ee14203b80be9c2/enklere/enklere-1/index.md"},
        :meta {:href "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/5d9c9172f29fa62f6d5c20177ee14203b80be9c2/enklere/enklere-1/meta.edn"}}
       {:doc/cohort [:cohort/id :cohort/enklere],
        :slug "enklere-2",
        :md {:href "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/5d9c9172f29fa62f6d5c20177ee14203b80be9c2/enklere/enklere-2/index.md"},
        :meta {:href "https://raw.githubusercontent.com/mikrobloggeriet/fjernkohortene/5d9c9172f29fa62f6d5c20177ee14203b80be9c2/enklere/enklere-2/meta.edn"}}),
      :rev "5d9c9172f29fa62f6d5c20177ee14203b80be9c2"})

  (def enklere (-> manifest :cohorts first))
  (def id->cohort (into {} (map (juxt :cohort/id identity)) (:cohorts manifest)))

  )
