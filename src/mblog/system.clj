(ns mblog.system
  (:require
   [clj-reload.core]
   [datomic.api :as d]
   [mblog.cohort :as cohort]
   [mblog.db :as db]
   [mblog.env :as env]
   [mblog.serve :as serve]
   [mblog.state :as state]
   [nextjournal.beholder :as beholder]
   [org.httpkit.server :as httpkit]
   [time-literals.read-write])
  (:import
   [java.time Instant]))

(defn create-datomic [_previous]
  (db/loaddb {:cohorts db/cohorts :authors db/authors}))
#_(alter-var-root #'state/datomic create-datomic)

(defn create-file-watcher [previous db]
  (when previous
    (beholder/stop previous))
  (when-not (System/getenv "GARDEN_GIT_REVISION")
    ;; Watch for changes in local development only.
    (let [roots (map :cohort/root (cohort/all db))]
      (apply beholder/watch
             (fn [_event]
               ;; NOTE: Current reloading behavior is "when ANY doc is changed,
               ;; reload EVERY doc". So there's possible performance to be
               ;; gained here.
               (let [the-docs (->> (cohort/all db)
                                   (mapcat db/find-cohort-docs))]
                 (alter-var-root #'state/datomic
                                 (fn [olddb]
                                   (-> olddb
                                       (d/with the-docs)
                                       :db-after)))))
             roots))))
#_(alter-var-root #'state/file-watcher (create-file-watcher state/datomic))

(defn create-injected-app [_previous]
  (fn [req]
    (let [ring-handler-var (resolve `serve/ring-handler)]
      (-> req
          (assoc :system/now (Instant/now))
          (assoc :system/datomic state/datomic)
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

(defn restart [previous start-fn & args]
  (apply start-fn previous args))

(defn ensure-started [previous start-fn & args]
  (if previous previous (apply start-fn nil args)))

(defonce !port (atom nil))

(defn ^:export start! [{:keys [port]}]
  (reset! !port port)
  (when (env/dev?) (dev-start!))
  (set! *print-namespace-maps* false)
  (time-literals.read-write/print-time-literals-clj!)
  (alter-var-root #'state/datomic ensure-started create-datomic)
  (alter-var-root #'state/file-watcher ensure-started create-file-watcher state/datomic)
  (alter-var-root #'state/injected-app ensure-started create-injected-app)
  (alter-var-root #'state/http-server ensure-started create-http-server (or port 7223)))
#_(start! {})

;; (defn after-ns-reload []
;;   (start! {:port @!port}))
