(ns mblog.theme
  (:require
   [mblog.contrast :as contrast]))

(defn generate-colors [theme]
  (-> (contrast/gen-colors-2 5 theme)
      (select-keys [:bg-color :text-color])))

(comment
  ;; COLOR GENERATION

  (defn generate [theme]
    (merge (generate-colors theme) (:fonts theme)))

  ;; fyll inn alle
  (generate {})
  ;; fyll inn én farge
  (generate {:bg-color [255 55 0] :font "Min font"})
  ;; fyll inn font
  (generate {:bg-color [12 88 188] :text-color [128 128 128]})

  ;; THEME
  {:bg-color "#fbe2f2"
   :text-color "#2406df"
   :font "\"Noto Serif\", serif"}

  ;; THEME STORAGE
  {"cca64a58-9feb-449c-a09a-681cd32a5b57"
   {:bg-color {:value [12 88 188]
               :locked? false}
    :text-color {:value [128 128 128]
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
                    ;; FIXME
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
