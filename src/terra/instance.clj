(ns terra.instance
  (:require
   [cheshire.core]
   [replicant.string]
   [ring.middleware.params]
   [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response on-open on-close]]
   [starfederation.datastar.clojure.api :as d*]))

(defn get-signals [req]
  (cheshire.core/parse-string (d*/get-signals req) keyword))

(defonce !sessions (atom {}))

(defn session-open [id {:keys [sse]}]
  (swap! !sessions assoc id {:sse sse}))

(defn session-close [id]
  (swap! !sessions dissoc id))

(defn sse-handler [req]
  (let [req (ring.middleware.params/params-request req)
        id (gensym)]
    (->sse-response req
                    {on-open (fn [sse] (session-open id {:sse sse :req req}))
                     on-close (fn [_ _] (session-close id))})))

(defn push! [pred html-str]
  (doseq [{:as session :keys [sse]} (vals @!sessions)]
    (when (pred session)
      (d*/patch-elements! sse html-str))))

(defn push-all! [html-str]
  (push! (constantly true) html-str))

(comment
  ;; Download/update Datastar
  (do (spit "public/js/datastar.js" (slurp d*/CDN-url))
      (spit "public/js/datastar.js.map" (slurp d*/CDN-map-url)))
  )
