(ns mblog.indigo
  (:require
   [hiccup.core]
   [mblog.env :as env]
   [mblog.hiccup :as hiccup]
   [mblog.samvirk :as samvirk]
   [mikrobloggeriet.doc :as doc]
   [terra.instance]))

(defn view-doc [doc]
  [:div.doc
   [:a {:name (:doc/slug doc)}]
   [:div
    (list
     ;; Pretend the slug is the title when the doc doesn't have a "real" title.
     (when-not (doc/cleaned-title doc)
       [:h1 (:doc/slug doc)])
     (->> doc
          doc/hiccup
          (hiccup/transform :img hiccup/lazyload)
          (hiccup/transform :iframe hiccup/lazyload)))]])

(defn render [{:keys [docs samvirk motto]}]
  {:headers (list [:meta {:charset "utf-8"}]
                  [:link {:rel "stylesheet" :href "css/styles/layout.css"}]
                  [:link {:rel "stylesheet" :href "css/styles/content.css"}]
                  [:link {:rel "stylesheet" :href (samvirk/font-path samvirk)}]
                  ;; Google fonts
                  [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
                  [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin ""}]
                  [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css2?family=Noto+Sans+Mono:wght@100..900&family=Noto+Sans:ital,wght@0,100..900;1,100..900&family=Noto+Serif:ital,wght@0,100..900;1,100..900&display=swap"}]
                  [:script {:type "module" :src "/js/datastar.js" :async true}]
                  [:style (:root samvirk)])
   :body (list [:header
                [:div.tags
                 [:div.tag "■ " (:bg-color samvirk)]
                 [:div.tag "□ " (:text-color samvirk)]
                 [:div.tag (samvirk/infer-main-font (samvirk/read-font samvirk))]]
                [:div.name-mottos
                 [:a {:href "/"} "Mikrobloggeriet"]
                 [:p motto]]]
               [:container
                [:section.navigation
                 [:nav
                  (for [doc docs]
                    [:a.navList.docSelector {:href (str "#" (:doc/slug doc))}
                     [:p.navTitle (doc/title-or-slug doc)]
                     [:div.navListData
                      [:p.navMeta (doc/created-date doc)]
                      [:p.navMeta "/"]
                      [:p.navMeta (:doc/slug doc)]]])]]
                [:section.content
                 (for [doc docs]
                   [:div.docView (view-doc doc)])]])})

(defn innhold->hiccup [innhold]
  (let [{:keys [headers body]} (render innhold)]
    [:html {:lang "en"}
     [:head headers]
     [:body
      [:span {:data-init "@get('/sse')" :style {:display "none"}}]
      [:div {:id "morph"} body]]]))

(def mottos
  ["Skaperglede. Levert."
   "Vi utforsker, vi opplever, vi forklarer."
   "Exploramus. Experimur. Explicamus."
   "Skrible. Notere. Knutre. Formulere."
   "Since 2023"
   "Upolert nysgjerrighet"])

(defonce !last-req (atom nil))
(defn last-req []
  (dissoc @!last-req :reitit.core/match))

(defn req->innhold [req]
  (reset! !last-req req)
  {:docs (-> req :mikrobloggeriet.system/datomic doc/latest)
   :samvirk (samvirk/load)
   :motto (rand-nth mottos)})

(defn hent-innhold!
  "Hent innhold uten å gå via noen request. Kun for lokal utvikling."
  []
  {:docs (-> (requiring-resolve 'mikrobloggeriet.state/datomic) deref doc/latest)
   :samvirk (samvirk/load)
   :motto (rand-nth mottos)})

(when (env/dev?)
  (let [{:keys [headers body]}
        (render (hent-innhold!))]
    (terra.instance/push-all! (hiccup.core/html [:div {:id "morph"} body]))
    (terra.instance/push-all! (hiccup.core/html [:head headers]))
    ))

(comment
  (set! *print-namespace-maps* false)

  (hent-innhold!)
  (last-req)

  :=)
