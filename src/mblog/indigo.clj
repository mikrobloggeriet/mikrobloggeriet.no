(ns mblog.indigo
  (:require
   [clojure.walk :refer [postwalk]]
   [datomic.api :as d]
   [hiccup.page]
   [mblog.samvirk :as samvirk]
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

(defn title-or-slug
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

(def mottos
  ["Skaperglede. Levert."
   "Vi utforsker, vi opplever, vi forklarer."
   "Exploramus, experimur, explicamus."
   "Skrible. Notere. Knutre. Formulere."])

(defn innhold->hiccup [{:keys [docs samvirk]}]
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
         [:a.navList.docSelector {:href (str "#" (:doc/slug doc))}
          [:p.navTitle (title-or-slug doc)]
          [:div.navListData
           [:p.navMeta (doc/created-date doc)]
           [:p.navMeta "/"]
           [:p.navMeta (:doc/slug doc)]]])]]
     [:section.content
      (for [doc docs]
        [:div.docView (view-doc doc)])]]]])

(defonce !last-req (atom nil))
(defn last-req []
  (dissoc @!last-req :reitit.core/match))

(defn req->innhold [req]
  (reset! !last-req req)
  {:docs (-> req :mikrobloggeriet.system/datomic doc/latest)
   :samvirk (samvirk/load)})

(comment
  (set! *print-namespace-maps* false)

  (do (require 'mikrobloggeriet.state)
      (def db mikrobloggeriet.state/datomic)
      (def docs (doc/latest db)))

  :=)
