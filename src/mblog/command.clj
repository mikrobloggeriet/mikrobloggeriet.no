(ns mblog.command
  (:require
   [clojure.string :as str]))

(defn post [command-id]
  (str "@post('/"
       (str/replace (namespace command-id) #"\." "/")
       "/"
       (name command-id)
       "')"))

(defn from-request [req]
  (-> req :reitit.core/match :path-params))
