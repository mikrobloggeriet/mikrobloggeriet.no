(ns mblog.indigo
  (:require
   [clojure.walk :refer [postwalk]]
   [datomic.api :as d]
   [hiccup.page]
   [mblog.samvirk :as samvirk]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [replicant.string]))

(defn hiccup-optmap [form]
  (if (map? (second form))
    (second form)
    {}))

(defn hiccup-children [form]
  (if (map? (second form))
    (drop 2 form)
    (drop 1 form)))

(defn lazyload-images [hiccup]
  (postwalk
   (fn [form]
     (cond
       (and (vector? form)
            (= (first form)
               :img))
       (into [:img (assoc (hiccup-optmap form)
                          :loading "lazy")]
             (hiccup-children form))
       :else form))
   hiccup))

(defn lazyload-iframes [hiccup]
  (postwalk
   (fn [form]
     (cond
       (and (vector? form)
            (= (first form)
               :iframe))
       (into [:iframe (assoc (hiccup-optmap form)
                             :loading "lazy")]
             (hiccup-children form))
       :else form))
   hiccup))

(defn find-title-ish
  "Finds the title if present, otherwise falls back to slug"
  [doc]
  (or (doc/cleaned-title doc)
      (:doc/slug doc)))

(defn view-doc [doc]
  [:div.doc
   [:a {:name (:doc/slug doc)}]
   [:div
    (list
     ;; Pretend the slug is the title when the doc doesn't have a "real" title.
     (when-not (doc/cleaned-title doc)
       [:h1 (:doc/slug doc)])
     (-> doc doc/hiccup lazyload-images lazyload-iframes))]])

(def samvirk
  {:bg-color "#91c1e9"
   :text-color "#1a2c5b"
   :font "font16.css"
   :root ":root {\n   --first100: rgb(145,193,233);\n   --first80: rgba(145,193,233, 0.8);\n   --first50: rgba(145,193,233, 0.5);\n   --first20: rgba(145,193,233, 0.2);\n   --first10: rgba(145,193,233, 0.1);\n   --second100: rgb(26,44,91);\n   --second80: rgba(26,44,91, 0.8);\n   --second50: rgba(26,44,91, 0.5);\n   --second20: rgba(26,44,91, 0.2);\n   --second10: rgba(26,44,91, 0.1);\n}"})

(def mottos
  ["Skaperglede. Levert."
   "Vi utforsker, vi opplever, vi forklarer."
   "Exploramus, experimur, explicamus."
   "Skrible. Notere. Knutre. Formulere."])

(defn innhold->hiccup [{:keys [docs current-cohort samvirk]}]
  (let [doc-visibility (fn [doc]
                         (when (and current-cohort
                                    (not= (:doc/cohort doc) current-cohort))
                           {:display "none"}))]
    [:html {:lang "en"}
     [:head
      [:meta {:charset "utf-8"}]
      [:link {:rel "stylesheet" :href "css/styles/layout.css"}]
      [:link {:rel "stylesheet" :href "css/styles/content.css"}]
      [:link {:rel "stylesheet" :href (samvirk/font-path samvirk)}]
      ;; Google fonts
      [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
      [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin ""}]
      [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css2?family=Noto+Sans+Mono:wght@100..900&family=Noto+Sans:ital,wght@0,100..900;1,100..900&family=Noto+Serif:ital,wght@0,100..900;1,100..900&display=swap"}]
      [:style (:root samvirk)]]
     [:body
      [:header
       [:div.tags
        [:div.tag "■ " (:bg-color samvirk)]
        [:div.tag "□ " (:text-color samvirk)]
        [:div.tag (samvirk/infer-main-font (samvirk/read-font samvirk))]]
       [:div.name-mottos
        [:a {:href "/"}
         "Mikrobloggeriet"]
        [:p (rand-nth mottos)]]]
      [:container 
       [:section.navigation
        [:nav
         (for [doc docs]
           [:a.navList.docSelector {:href (str "#" (:doc/slug doc))
                                    :style (doc-visibility doc)
                                    :data-cohort (-> doc :doc/cohort :cohort/slug)}
            [:p.navTitle (find-title-ish doc)]
            [:div.navListData
             [:p.navMeta (doc/created-date doc)] 
             [:p.navMeta "/"]
             [:p.navMeta (:doc/slug doc)]]])]]
       [:section.content
        [:div (for [doc docs]
                [:div.docView {:style (doc-visibility doc)
                               :data-cohort (-> doc :doc/cohort :cohort/slug)}
                 (view-doc doc)])]]]
      ]]))

(defonce !last-req (atom nil))
(defn last-req []
  (dissoc @!last-req :reitit.core/match :mikrobloggeriet.system/pageviews))
#_(last-req)

(defn req->innhold [req]
  (reset! !last-req req)
  (let [db (:mikrobloggeriet.system/datomic req)]
    (merge {:docs (doc/latest db)
            :samvirk (samvirk/load)}
           (when-let [cohort-slug (get-in req [:query-params "cohort"])]
             {:current-cohort (d/entity db [:cohort/slug cohort-slug])}))))

(comment
  (set! *print-namespace-maps* false)

  (do (require 'mikrobloggeriet.state)
      (def db mikrobloggeriet.state/datomic)
      (def docs (doc/latest db)))

  :=)
