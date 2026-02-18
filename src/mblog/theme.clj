(ns mblog.theme
  (:require
   [duratom.core :refer [duratom]]
   [mblog.contrast :as contrast]
   [mblog.fonts :as fonts]))

(defn generate-colors [theme]
  (-> (contrast/gen-colors-2 5 theme)
      (select-keys [:bg-color :text-color])))

(defn generate-font [theme]
  (cond-> theme
    (not (:font theme))
    (assoc :font (fonts/random))))

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

(def lock-all #{:text-color :font :bg-color})

(defn set-locks! [session locks]
  (swap! store #(assoc-in % [session :locked] locks)))

(comment
  (def teodor "6d1df619-5348-4735-a7d5-36d939ccf965")
  (set-locks! teodor lock-all) ; lock
  (set-locks! teodor #{}) ; unlock

  @store
  (def session "fcd826c2-8b1b-4b81-951f-9af735a02a37")
  (reset! store {})
  (swap! store
         (fn [old]
           (assoc-in old [session :locked] #{:text-color :font})))
  (get @store session))
