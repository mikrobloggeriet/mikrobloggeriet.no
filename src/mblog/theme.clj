(ns mblog.theme
  (:require
   [mblog.contrast :as contrast]))

(defn generate-colors [theme]
  (let [{:keys [c1 c2]} (contrast/gen-colors 5)]
    (merge {:bg-color c1 :text-color c2}
           theme)))

(comment
  ;; COLOR GENERATION

  (defn generate [theme]
    (merge (generate-colors theme) (:fonts theme)))

  ;; fyll inn alle
  (generate {})
  ;; fyll inn én farge
  (generate {:bg-color "crimson" :font "Min font"})
  ;; fyll inn font
  (generate {:bg-color "crimson" :text-color "grey"})

  ;; THEME
  {:bg-color "#fbe2f2"
   :text-color "#2406df"
   :font "\"Noto Serif\", serif"}

  ;; THEME STORAGE
  {"cca64a58-9feb-449c-a09a-681cd32a5b57"
   {:bg-color {:value "#fbe2f2"
               :locked? false}
    :text-color {:value "#2406df"
                 :locked? false}
    :font {:value "\"Noto Serif\", serif"
           :locked? false}}}

  :=)

(defn create-overrides [theme-state]
  (into {}
        (->> theme-state
             (filter (fn [[_ {:keys [locked?]}]]
                       (= false locked?)))
             (map (fn [[k _]]
                    [k {:value "crimson"
                        :locked? false}])))))

(defn update-and-get* [store session-id]
  (-> (swap! store update session-id
             (fn [theme-state]
               (merge {:bg-color "a"}
                      theme-state
                      (create-overrides theme-state))))
      (get session-id)))

(defonce store (atom {}))

(defn update-and-get [session-id]
  (update-and-get* store session-id))
