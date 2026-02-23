(ns mblog.command-registry
  (:require
   [mblog.command :as command]
   [mblog.http :as http]
   [mblog.theme :as theme]))

(def registry
  {"theme" #'theme/handle})

(defn receive [req]
  (let [{:keys [group slug]} (command/from-request req)]
    (if-let [handler (registry group)]
      (do (handler group slug (http/find-session req) req)
          {:status 202})
      {:status 404})))
