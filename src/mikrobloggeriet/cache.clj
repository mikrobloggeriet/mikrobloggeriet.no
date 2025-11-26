(ns mikrobloggeriet.cache
  "Simple atom-backed caching for Clojure functions

  Bring your own atom, use duration if you want a durable cache."
  (:require
   [clojure.string :as str]
   [duratom.core :refer [duratom]]
   [hickory.core :as hickory]
   [lookup.core :as lookup]
   [mikrobloggeriet.pandoc :as pandoc]
   [nextjournal.markdown :as md]))


(defn cache-fn-by
  "A simple in-memory caching mechanism

  Example usage:

    (def cached+ (cache-fn-by slow+ (atom {}) (fn [a b] (str a \" \" b)))

    (cached+ 1 2) ; normal speed first time
    (cached+ 1 2) ; next invokation is a cache lookup

  - `cache-atom` is the atom to use as cache.

  - `f` is the function you want to cache

  - `cache-key-fn` is a function taking the same arguments as `f`, returning a
    string which is used as cache key.

  - `warn-fn` (optional, function from string to any) is called when the caching
    mechanism fails. It defaults to nil, no reporting."
  ([cache-atom f cache-key-fn]
   (cache-fn-by cache-atom f cache-key-fn {}))
  ([cache-atom f cache-key-fn {:keys [warn-fn]}]
   (fn [& args]
     (let [cache-key (apply cache-key-fn args)]
       (if (contains? @cache-atom cache-key)
         (get @cache-atom cache-key)
         (let [result (apply f args)]
           (if (string? cache-key)
             (swap! cache-atom assoc cache-key result)
             ;; Otherwise, warn that the cache key isn't valid
             (when warn-fn
               (if-let [fn-name (:name (meta f))]
                 (warn-fn "Warning:" 'fn-with-cache "recived a non-string cache key wrapping" fn-name)
                 (warn-fn "Warning:" 'fn-with-cache "recived a non-string cache key"))))
           result))))))

(comment
  (defn slow+ [a b]
    (Thread/sleep 200)
    (+ a b))

  (slow+ 10 20)
  ;; slow

  (def fast+ (cache-fn-by (atom {}) slow+ #(str %1 " " %2)))
  ;; fast after values have been cached.

  (fast+ 11 2)
  (fast+ 1 12)
  (fast+ 9 999)
  (fast+ 99 99))

(def pandoc-cache-atom
  ^{:doc "Disk-backed cache atom when disk (GARDEN_STORAGE) is available"}
  (when-let [storage-path (System/getenv "GARDEN_STORAGE")]
    (duratom :local-file
             :file-path (str storage-path "/mikrobloggeriet.htmlcache.edn")
             :commit-mode :sync
             :init {})))

(defn parse-html [html-str]
  (->> html-str
       hickory/parse
       hickory/as-hiccup
       (lookup/select '[html body])
       first
       rest))

(defn parse-markdown* [markdown-str]
  (let [pandoc (pandoc/from-markdown markdown-str)
        html-str (str/trim (pandoc/to-html pandoc))]
    {:doc/html html-str
     :doc/hiccup (parse-html html-str)
     :title (pandoc/infer-title pandoc)
     :description (pandoc/infer-description pandoc)}))

(def parse-markdown
  (cache-fn-by (or pandoc-cache-atom (atom {}))
               #'parse-markdown*
               #(str "2025-03-19-journal"
                     "\n" %)
               identity))

(comment
  (parse-markdown "# Funksjonell programmering")

  (->> @pandoc-cache-atom
       vals
       (map :description)
       (filter identity))

  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; What if we use nextjournal/markdown?

(require 'replicant.string)

(defn find-title [ast]
  (let [first-node (-> ast :content first)]
    (when (= :heading (:type first-node))
      (md/node->text first-node))))

(defn find-description [ast]
  (some->> ast
           :content
           (filter (comp #{:paragraph} :type))
           md/node->text))

(defn parse-markdown2* [markdown-str]
  (let [ast (md/parse markdown-str)
        hiccup (md/->hiccup (assoc md/default-hiccup-renderers
                                   :html-block (fn [_ m]
                                                 [:div "LOL ugyldig HTML!"]))
                            ast)]
    {:doc/html (replicant.string/render hiccup)
     :doc/hiccup hiccup
     :title (find-title ast)
     :description (find-description ast)}))

(def nextjournal-cache-atom
  ^{:doc "Disk-backed cache for nextjournal markdown when disk (GARDEN_STORAGE) is available"}
  (when-let [storage-path (System/getenv "GARDEN_STORAGE")]
    (duratom :local-file
             :file-path (str storage-path "/mikrobloggeriet.nextjournal-cache.edn")
             :commit-mode :sync
             :init {})))

(def parse-markdown2
  (cache-fn-by (or nextjournal-cache-atom (atom {}))
               #'parse-markdown2*
               #(str "2025-11-26 7"
                     "\n" %)
               identity))

(comment

  ;; Neste hinder for ny Markdown-parser er inline HTML.
  ;; Vi har inline HTML i feks OLORM-5.
  ;; Det gir en feilmelding i HTML-en i stedet for rendret HTML.

  (def html-in-md
    "
<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->
")

  (-> html-in-md md/parse)


  (-> html-in-md md/parse md/->hiccup)
  ;; => [:div
  ;;     [:span.message.red
  ;;      [:strong "Unknown type: ':html-block'."]
  ;;      [:code
  ;;       "{:type :html-block, :content [{:type :text, :text \"<!-- 1. Hva gjør du akkurat nå? -->\"}]}"]]
  ;;     [:span.message.red
  ;;      [:strong "Unknown type: ':html-block'."]
  ;;      [:code
  ;;       "{:type :html-block, :content [{:type :text, :text \"<!-- 2. Finner du kvalitet i det? -->\"}]}"]]
  ;;     [:span.message.red
  ;;      [:strong "Unknown type: ':html-block'."]
  ;;      [:code
  ;;       "{:type :html-block, :content [{:type :text, :text \"<!-- 3. Hvorfor / hvorfor ikke? -->\"}]}"]]]


  )
