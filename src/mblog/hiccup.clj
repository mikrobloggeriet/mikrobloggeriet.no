(ns mblog.hiccup
  (:require
   [clojure.walk :refer [postwalk]]))

(defn tag [form]
  (when (vector? form) (first form)))

(defn optmap [form]
  (if (map? (second form))
    (second form)
    {}))

(defn children [form]
  (if (map? (second form))
    (drop 2 form)
    (drop 1 form)))

(defn transform [tag f hiccup]
  (postwalk (fn [form]
              (cond (and (vector? form) (= (first form) tag))
                    (f form)
                    :else form))
            hiccup))

(defn update-optmap [form f & args]
  (into [(tag form) (apply f (optmap form) args)] (children form)))

(def lazyload #(update-optmap % assoc :loading "lazy"))
