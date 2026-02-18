(ns mblog.theme
  (:require
   [duratom.core :refer [duratom]]
   [mblog.contrast :as contrast]))

(defn generate-colors [theme]
  (-> (contrast/gen-colors-2 5 theme)
      (select-keys [:bg-color :text-color])))

(defn generate-font [theme]
  {:font "\"Noto Serif\", serif"})

(defn generate [theme]
  (merge (generate-colors theme)
         (generate-font theme)))

(defn get-locked [theme-state]
  (select-keys (:theme theme-state) (:locked theme-state)))

(defn update-and-get* [store session-id]
  (-> (swap! store update session-id
             (fn [theme-state]
               (assoc theme-state :theme
                      (generate (get-locked theme-state)))))
      (get session-id)))

(defonce store
  (if-let [storage-path (System/getenv "GARDEN_STORAGE")]
    (duratom :local-file
             :file-path (str storage-path "/mikrobloggeriet.themes.edn")
             :commit-mode :sync
             :init {})
    (atom {})))

(defn update-and-get [session-id]
  (update-and-get* store session-id))

(comment
  (reset! store {}))
