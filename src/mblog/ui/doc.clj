(ns mblog.ui.doc
  (:require
   [datomic.api :as d]
   [mblog.doc :as doc]
   [mblog.http :as http]
   [mblog.indigo :as indigo]
   [mblog.samvirk :as samvirk]
   [mblog.theme :as theme]))

(def mobile-menu? false)

(defn doc->href [doc]
  (str "/doc/" (:doc/slug doc)))

(defn doc->href-FUTURE-AWESOME
  ;; Neno og Teodor har future-proofet litt.
  ;; Vi ønsker å ha *en* URL per dokument.
  ;; I dag har vi to.
  ;; Feks:
  ;;  /doc/enklere-1
  ;;  /enklere/enklere-1
  ;;
  ;; Det kan vi løse ved å kun bruke /enklere/enklere-1 - fordi det er den gamle URL-en.
  ;;
  ;; MEN den endringen klarer vi ikke gjøre nå, fordi det ikke er *en* route som
  ;; lager /enklere/enklere-1, men mange. /*/*/ er en for bred match!
  ;;
  ;; Så det blir et problem for framtidens Neno og framtidens Teodor.
  [doc]
  (str "/" (-> doc :doc/cohort :cohort/slug)
       "/" (:doc/slug doc)
       "/"))

(defn navigator [text doc class] 
  [:a {:href (doc->href doc)
       :class class} 
   text " " (doc/title-or-slug doc)])

(defn innhold->hiccup [{:keys [doc docs samvirk next prev]}]
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
    [:script {:type "module" :src "/js/datastar.js" :async true}]
    [:style (:root samvirk)]]
   [:body
    [:header
     [:div.tags
      [:button.tag {:data-on:click "@post('/debug')"}
       #_[:img {:src "/icons/lock.svg"}]
       "■ " (:bg-color samvirk)]
      [:div.tag "□ " (:text-color samvirk)]
      [:div.tag (samvirk/infer-main-font (samvirk/read-font samvirk))]]
     [:div.name-mottos
      [:a {:href "/"} "Mikrobloggeriet"]
      [:p (rand-nth indigo/mottos)]]
     (when mobile-menu?
       (list
        [:input#mobile-menu-toggle.mobile-menu-toggle {:type "checkbox"}]
        [:label.mobile-menu-button {:for "mobile-menu-toggle"} "Meny"]
        [:nav.mobile-nav
         [:ul
          [:li [:a {:href "#"} "Item 1"]]
          [:li [:a {:href "#"} "Item 2"]]
          [:li [:a {:href "#"} "Item 3"]]
          [:li [:a {:href "#"} "Item 4"]]]]))]
    [:container
     [:section.navigation
      [:nav
       (for [linked-doc docs]
         [:a.navList.docSelector {:href (doc->href linked-doc)
                                  :class (when (= doc linked-doc)
                                           "selected")}
          [:p.navTitle (doc/title-or-slug linked-doc)]
          [:div.navListData
           [:p.navMeta (doc/created-date linked-doc)]
           [:p.navMeta "/"]
           [:p.navMeta (:doc/slug linked-doc)]]])]]
     (when doc
       [:section.content (indigo/view-doc doc) 
        [:div.doc-navigation
         (when prev (navigator "<" prev "before"))
         (when next (navigator ">" next "after"))]])]
    ]])

(defn req->innhold [req]
  (let [datomic (:system/datomic req)
        slug (-> req :reitit.core/match :path-params :slug)]
    (-> (doc/find+nav datomic slug)
        (assoc :docs (doc/latest datomic))
        (assoc :samvirk (samvirk/load (:theme (theme/update-and-get (http/find-session req))))))))

(comment
  (require 'mblog.state)
  (def doc (d/entity mblog.state/datomic
                     [:doc/slug "olorm-1"]))

  (:doc/slug doc)
  ;; => "olorm-1"

  (:cohort/slug (:doc/cohort doc))
  ;; => "olorm"
  )
