(ns mblog.theme.nice
  "Just some themes, written down cause they are nice"
  (:require
   [mblog.theme :as theme]))

(def grass
  {:text-color [25 24 0]
   :bg-color [146 250 164]
   :font "font1.css"})

(def paper
  {:text-color [0 0 0]
   :bg-color [255 255 255]
   :font "font1.css"})

(->> @theme/store
     (filter (comp :locked val)))

(defn set-theme [session-id theme]
  (swap! theme/store assoc session-id
         {:theme theme
          :locked #{:text-color :font :bg-color}}))

(comment
  ;; 1 - finn deg selv
  (def self "a7eab...")

  ;; 2 - velg tema!
  (set-theme self paper)
  )
