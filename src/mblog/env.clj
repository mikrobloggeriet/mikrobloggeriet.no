(ns mblog.env
  (:require
   [clojure.string :as str]))

(defn env []
  (let [url (System/getenv "GARDEN_URL")]
    (cond (str/starts-with? url "Http://localhost")
          :env/dev

          (str/starts-with? url "https://mikrobloggeriet.no")
          :env/prod

          :else
          :env/unkonwn)))

(def dev? #(= :env/dev (env)))
(def prod? #(= :env/prod (env)))
(def unknown? #(= :env/unknown (env)))
