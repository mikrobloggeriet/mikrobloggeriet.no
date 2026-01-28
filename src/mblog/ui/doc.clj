(ns mblog.ui.doc
  (:require
   [datomic.api :as d]
   [mblog.doc :as doc]
   [mblog.indigo :as indigo]
   [mblog.samvirk :as samvirk]))

(defn doc->href [doc]
  (str "/doc/" (:doc/slug doc)))

(defn doc->hiccup [{:keys [doc docs samvirk]}]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
    [:link {:rel "stylesheet" :href "/css/styles/layout.css"}]
    [:link {:rel "stylesheet" :href "/css/styles/content.css"}]
    [:link {:rel "stylesheet" :href (samvirk/font-path samvirk)}]
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
      [:a {:href "/"} "Mikrobloggeriet"]
      [:p (rand-nth indigo/mottos)]]]
    [:container
     [:section.navigation
      [:nav
       (for [doc docs]
         [:a.navList.docSelector {:href (doc->href doc)}
          [:p.navTitle (doc/title-or-slug doc)]
          [:div.navListData
           [:p.navMeta (doc/created-date doc)]
           [:p.navMeta "/"]
           [:p.navMeta (:doc/slug doc)]]])]]
     [:section.content
      [:div.docView (indigo/view-doc doc)]]]
    [:footer
     [:a {:href "/"}
      [:p "Mikrobloggeriet"]]]]])

(defn req->innhold [req]
  (let [datomic (:mikrobloggeriet.system/datomic req)
        slug (-> req :reitit.core/match :path-params :slug)]
    {:docs (doc/latest datomic)
     :doc (d/entity datomic [:doc/slug slug])
     :samvirk (samvirk/load)}))

(comment
  (require 'mikrobloggeriet.state)
  (def olorm-1 (d/entity mikrobloggeriet.state/datomic
                         [:doc/slug "olorm-1"]))

  (doc->href olorm-1)

  )
