(ns mblog.system
  (:require
   [clj-reload.core]
   [mblog.cohort :as cohort]
   [mblog.db :as db]
   [mblog.env :as env]
   [mblog.serve :as serve]
   [mblog.state :as state]
   [mblog.page-registry :as page-registry]
   [nextjournal.beholder :as beholder]
   [org.httpkit.server :as httpkit]
   [time-literals.read-write])
  (:import
   [java.time Instant]))

(defn create-datomic [_previous]
  (db/loaddb {:cohorts db/cohorts :authors db/authors}))
#_(alter-var-root #'state/datomic create-datomic)

(defn watch-for-new-documents? []
  (not (System/getenv "GARDEN_GIT_REVISION")))

(defn create-file-watcher [previous db]
  (when previous
    (beholder/stop previous))
  (when (watch-for-new-documents?)
    ;; Watch for changes in local development only.
    (let [roots (map :cohort/root (cohort/all db))]
      (apply beholder/watch
             (fn [_event]
               ;; Current reloading behavior:
               ;; When any doc is changed, reload every doc.
               ;; There's more performance to be had if desired.
               (alter-var-root #'state/datomic
                               (fn [_olddb] (db/add-docs db (db/load-docs db)))))
             roots))))
#_(alter-var-root #'state/file-watcher create-file-watcher state/datomic)

(defn create-injected-app [_previous]
  (fn [req]
    (let [ring-handler-var (resolve `serve/ring-handler)
          page-registry-var (resolve `page-registry/registry)]
      (-> req
          (assoc :system/now (Instant/now))
          (assoc :system/datomic state/datomic)
          (assoc :system/page-registry (deref page-registry-var))
          (assoc :request/id (str (random-uuid)))
          ring-handler-var))))
#_(alter-var-root #'state/injected-app create-injected-app)

(defn create-http-server [previous port]
  (when previous
    (httpkit/server-stop! previous))
  (httpkit/run-server (fn [req]
                        (state/injected-app req))
                      {:port port
                       :legacy-return-value? false}))
#_(alter-var-root #'state/http-server (create-http-server 7223))

(defn dev-start! []
  (require 'dev))

(defn ensure-started [previous start-fn & args]
  (if previous previous (apply start-fn nil args)))

(defonce !port (atom nil))

(defn ^:export start! [{:keys [port]}]
  (reset! !port port)
  (when (env/dev?) (dev-start!))
  (set! *print-namespace-maps* false)
  (time-literals.read-write/print-time-literals-clj!)
  (alter-var-root #'state/datomic create-datomic)
  (alter-var-root #'state/file-watcher create-file-watcher state/datomic)
  (alter-var-root #'state/injected-app create-injected-app)
  ;; HTTP server cannot be restarted in process, because Application.Garden
  ;; requires a running HTTP server.
  (alter-var-root #'state/http-server ensure-started create-http-server (or port 7223)))
#_(start! {})

(defn after-ns-reload []
  (when (System/getenv "MBLOG_RESTART_ON_RELOAD")
    (start! {:port @!port})))
