(ns mblog.theme
  (:require
   [duratom.core :refer [duratom]]
   [mblog.contrast :as contrast]
   [mblog.fonts :as fonts]
   [clojure.string :as str]))

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

(defn lock [state session-id k]
  (update-in state
             [session-id :locked]
             (fn [old] ((fnil conj #{}) old k))))

(defn unlock [state session-id k]
  (update-in state
             [session-id :locked]
             (fn [old] (disj old k))))

(defn parse-slug [slug]
  (mapv keyword (str/split slug #"\-" 2)))

(defn handle [group slug session-id _req]
  (let [[action k] (parse-slug slug)]
    (cond
      (= action :lock)
      (swap! store lock session-id k)

      (= action :unlock)
      (swap! store unlock session-id k)

      :else
      (throw (ex-info "Invalid theme action"
                      {:group group :slug slug :session-id session-id :action k})))))
