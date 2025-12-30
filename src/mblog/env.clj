(ns mblog.env
  (:require
   [clojure.string :as str]))

(defn env* [url]
  (cond (not url)
        :env/unknown

        (str/starts-with? url "http://localhost")
        :env/dev

        (str/starts-with? url "https://mikrobloggeriet.no")
        :env/prod

        :else
        :env/unknown))

(defn env [] (env* (System/getenv "GARDEN_URL")))

(def dev? #(= :env/dev (env)))
(def prod? #(= :env/prod (env)))
(def unknown? #(= :env/unknown (env)))

(comment
  (env)

  :=)
