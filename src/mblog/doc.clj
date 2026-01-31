(ns mblog.doc
  (:refer-clojure :exclude [next])
  (:require
   [clojure.string :as str]
   [datomic.api :as d]
   [mblog.cache :as cache]))

(defn created-date [doc]
  (some-> doc :doc/created
          (str/split #"T") first))

(defn number [doc]
  (when-let [slug (:doc/slug doc)]
    (parse-long (last (str/split slug #"-")))))

(defn href
  [doc]
  (when-let [doc-slug (:doc/slug doc)]
    (str "/doc/" doc-slug)))

(defn previous [db doc]
  (let [previous-number (dec (number doc))
        cohort (:doc/cohort doc)
        previous-slug (str (:cohort/slug cohort) "-" previous-number)]
    (d/entity db [:doc/slug previous-slug])))

(defn next [db doc]
  (let [next-number (inc (number doc))
        cohort (:doc/cohort doc)
        next-slug (str (:cohort/slug cohort) "-" next-number)]
    (d/entity db [:doc/slug next-slug])))

(defn author-first-name [db doc]
  (:author/first-name (d/entity db [:author/email (:git.user/email doc)])))

(defn title [doc]
  (or (:doc/title doc)
      (some-> doc :doc/markdown cache/parse-markdown :title)))

(defn html [doc]
  (:doc/html (cache/parse-markdown (:doc/markdown doc))))

(defn hiccup [doc]
  (:doc/hiccup (cache/parse-markdown (:doc/markdown doc))))

(defn description [doc]
  (:description (cache/parse-markdown (:doc/markdown doc))))

(defn remove-cohort-prefix [title]
  (str/replace title #"^[A-Z]+-\d+[: -]+ " ""))

(defn cleaned-title [doc]
  (some-> doc title remove-cohort-prefix))

(defn title-or-slug [doc]
  (or (cleaned-title doc) (:doc/slug doc)))

(defn all [db]
  (->> (d/q '[:find [?eid ...]
              :where [?eid :doc/slug]]
            db)
       (map (fn [eid]
              (d/entity db eid)))))

(defn latest [db]
  (->> (all db)
       (filter :doc/created)
       (sort-by (juxt :doc/created number cleaned-title))
       reverse))

(defn random-published [db]
  (rand-nth (->> (all db)
                 (remove :doc/draft?))))

(defn find+nav
  "Finn dokument per slug, returner også før- og etter"
  [db slug]
  (let [chronologically (into [] (latest db))
        [idx doc] (some->> chronologically
                           (map-indexed vector)
                           (filter (fn [[_ doc]]
                                     (= slug (:doc/slug doc))))
                           first)]
    (when doc
      (let [next (get chronologically (inc idx))
            prev (get chronologically (dec idx))]
        (cond-> {:doc doc}
          next (assoc :next next)
          prev (assoc :prev prev))))))
