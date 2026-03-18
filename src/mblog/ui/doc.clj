(ns mblog.ui.doc
  (:require
   [datomic.api :as d]
   [mblog.command :as command]
   [mblog.doc :as doc]
   [mblog.vision :as vision]
   [mblog.hiccup :as hiccup]
   [mblog.http :as http]
   [mblog.samvirk :as samvirk]
   [mblog.theme :as theme]
   [mblog.xlink :as xlink]))

(def mobile-menu? false)

(defn navigator [text doc class]
  [:a {:href (xlink/doc doc)
       :class class}
   text " " (doc/title-or-slug doc)])

(defn toggler [locked? unlock-cmd lock-cmd body]
  (if locked?
    [:button.tag.locked {:data-on:click (command/post unlock-cmd)}
     body]
    [:button.tag.unlocked {:data-on:click (command/post lock-cmd)}
     body]))

(defn view-doc [doc]
  [:read-doc
   [:div
    (list
     ;; Pretend the slug is the title when the doc doesn't have a "real" title.
     (when-not (doc/cleaned-title doc)
       [:h1 (:doc/slug doc)])
     (->> doc
          doc/hiccup
          (hiccup/transform :img hiccup/lazyload)
          (hiccup/transform :iframe hiccup/lazyload)))]])

(defn innhold->hiccup [{:as opts :keys [doc docs samvirk next prev]}]
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
      (toggler (contains? (:theme/locked opts) :text-color)
               :command.theme/unlock-text-color
               :command.theme/lock-text-color
               (list "■ " (:text-color samvirk)))
      (toggler (contains? (:theme/locked opts) :bg-color)
               :command.theme/unlock-bg-color
               :command.theme/lock-bg-color
               (list "□ " (:bg-color samvirk)))
      (toggler (contains? (:theme/locked opts) :font)
               :command.theme/unlock-font
               :command.theme/lock-font
               (samvirk/infer-main-font (samvirk/read-font samvirk)))
      [:a {:href (xlink/les doc)} "les fokusert"]]
     [:div.name-mottos
      [:a#tittel {:href "/"} "Mikrobloggeriet"]
      [:p (rand-nth vision/mottos)]]
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
     [:section.doc-selector
      [:nav
       (for [linked-doc docs]
         [:a.navList.docSelector {:href (xlink/doc linked-doc)
                                  :class (when (= doc linked-doc)
                                           "selected")}
          [:p.navTitle (doc/title-or-slug linked-doc)]
          [:div.navListData
           [:p.navMeta (doc/created-date linked-doc)]
           [:p.navMeta "/"]
           [:p.navMeta (:doc/slug linked-doc)]]])]]
     (when doc
       [:section.content
        [:article
         (view-doc doc)
         [:nav
          (when prev (navigator "<" prev "before"))
          (when next (navigator ">" next "after"))]]])]]])

(defonce !last (atom nil))

(defn parse-request [req]
  (reset! !last req)
  (-> (select-keys req
                   [:session/id
                    :system/datomic
                    :request/id])
      (assoc :doc/slug (-> req :reitit.core/match :path-params :slug))))

(defn req->innhold [req]
  (let [datomic (:system/datomic req)
        {:keys [theme locked]} (theme/update-and-get (:session/id req))]
    (-> (doc/find+nav datomic (:doc/slug req))
        (assoc :docs (doc/latest datomic))
        (assoc :samvirk (samvirk/load theme))
        (assoc :theme/locked locked))))

(comment
  (-> @!last keys)
  (-> @!last parse-request keys)

  (set! *print-namespace-maps* false)
  (d/entity mblog.state/datomic [:doc/slug "enklere-3"])
  )
