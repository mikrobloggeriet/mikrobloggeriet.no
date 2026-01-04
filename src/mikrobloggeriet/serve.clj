(ns mikrobloggeriet.serve
  (:require
   [babashka.fs :as fs]
   [clj-simple-stats.core]
   [clojure.pprint]
   [datomic.api :as d]
   [mblog.dsminimal :as dsminimal]
   [mblog.indigo]
   [mblog.page-machinery :as page-machinery]
   [mblog.page-registry :as page-registry]
   [mikrobloggeriet.cohort.urlog :as cohort.urlog]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.feed :as feed]
   [mikrobloggeriet.http :as http]
   [mikrobloggeriet.ui.cohort :as ui.cohort]
   [mikrobloggeriet.ui.doc :as ui.doc]
   [mikrobloggeriet.ui.editor :as ui.editor]
   [reitit.ring.middleware.parameters]
   [reitit.ring]
   [ring.middleware.gzip]
   [ring.middleware.params]
   [terra.assetwatch :as assetwatch]
   [terra.instance]))

(defn random-doc [req]
  (let [db (:mikrobloggeriet.system/datomic req)
        target (or
                (when-let [doc (doc/random-published db)]
                  (doc/href doc))
                "/")]
    {:status 307 ;; temporary redirect
     :headers {"Location" target}
     :body ""}))

(defn last-modified-file [root match]
  (apply max-key
         (comp fs/file-time->millis fs/last-modified-time)
         (fs/glob root match)))

(defn deploy-info [_req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body
   (with-out-str
     (clojure.pprint/pprint
      {:last-modified-file-time
       (str (fs/last-modified-time
             (last-modified-file "." "**/*.{js,css,html,clj,md,edn}")))}))})

(defn health [_req]
  {:status 200 :headers {"Content-Type" "text/plain"} :body "all good!"})

(defn markdown-cohort-routes [cohort-data]
  [(str "/" (:cohort/slug cohort-data))
   ["/" {:get (fn [req]
                (let [db (:mikrobloggeriet.system/datomic req)
                      cohort (d/entity db [:cohort/slug (:cohort/slug cohort-data)])]
                  (ui.cohort/doc-table db cohort req)))
         :name (keyword (str "mikrobloggeriet." (:cohort/slug cohort-data))
                        "all")}]
   ["/:slug/" {:get (fn [req]
                      (let [db (:mikrobloggeriet.system/datomic req)
                            doc-slug (http/path-param req :slug)
                            doc (d/entity db [:doc/slug doc-slug])]
                        (ui.doc/page doc
                                     req
                                     (merge
                                      (when-let [previous (doc/previous db doc)]
                                        {:previous previous})
                                      (when-let [next (doc/next db doc)]
                                        {:next next})))))
               :name (keyword (str "mikrobloggeriet." (:cohort/slug cohort-data))
                              "doc")}]])

(comment
  (markdown-cohort-routes (:cohort/olorm db/cohorts)))

(defn serve-page
  "Serves any page from the page registry"
  [request]
  (when-let [page-id (-> request :reitit.core/match :data :name)]
    (when-let [page (get page-registry/registry page-id)]
      (page-machinery/respond request page))))

(defn create-ring-handler
  []
  (reitit.ring/ring-handler
   (reitit.ring/router
    (concat

     [["/" {:get #'serve-page
            :head #'health ;; HEAD / is Application.Garden's health check
            :name :page/indigo}]

      ["/content-design" {:get #'serve-page
                          :name :page/content-design}]

      ["/sse" {:get #'terra.instance/sse-handler
               :name :terra.instance/sse-handler}]

      ["/doc/:slug" {:get #'serve-page
                     :name :page/doc}]]

     ;; Markdown cohorts
     (for [c (->> (vals db/cohorts)
                  (filter #(= :cohort.type/markdown (:cohort/type %))))]
       (markdown-cohort-routes c))

     ;; Urlog
     [["/urlog/" {:get #'cohort.urlog/page
                  :name :mikrobloggeriet.urlog/all}]]

     ;; Support old URLs
     ;; Originally, OLORM was /o/ and JALS was /j/.
     [["/o/" {:get (constantly (http/permanent-redirect {:target "/olorm/"}))}]
      ["/j/" {:get (constantly (http/permanent-redirect {:target "/jals/"}))}]
      ["/o/:slug/" {:get (fn [req]
                           (when-let [slug (http/path-param req :slug)]
                             (http/permanent-redirect {:target (str "/olorm/" slug "/")})))}]
      ["/j/:slug/" {:get (fn [req]
                           (when-let [slug (http/path-param req :slug)]
                             (http/permanent-redirect {:target (str "/jals/" slug "/")})))}]]

     ;; DIV
     [ ;; Go to a random document
      ["/random-doc" {:get #'random-doc
                      :name :mikrobloggeriet/random-doc}]

      ;; Deploy
      ["/deploy-info" {:get #'deploy-info
                       :name :mikrobloggeriet/deploy-info}]

      ;; Et forsøk på å redigere tekst direkte fra nettleseren
      ["/editor/" {:get #'ui.editor/page
                   :name :mikrobloggeriet/edit}]

      ;; Helsesjekk
      ["/health" {:get health
                  :name :mikrobloggeriet/health}]

      ["/feed.xml" {:get #'feed/handler}]]

     ;; Datastar-eksperiment
     [["/dsminimal" {:handler #'dsminimal/home}]
      ["/dsminimal-messsage" {:handler #'dsminimal/hello-world
                              :middleware [reitit.ring.middleware.parameters/parameters-middleware]}]]))
   (reitit.ring/routes
    (reitit.ring/redirect-trailing-slash-handler)
    #'assetwatch/handler)))

(assetwatch/watch! "public")

(def router (create-ring-handler))

(def ring-handler
  (-> router
      (clj-simple-stats.core/wrap-stats
       {:db-path (str (System/getenv "GARDEN_STORAGE") "/clj_simple_stats.duckdb")})
      ring.middleware.params/wrap-params
      ring.middleware.gzip/wrap-gzip))
