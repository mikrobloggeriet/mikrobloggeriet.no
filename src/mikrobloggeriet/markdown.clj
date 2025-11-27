(ns mikrobloggeriet.markdown
  (:require [nextjournal.markdown :as md]
            [replicant.string]))

(defn find-title [ast]
  (let [first-node (-> ast :content first)]
    (when (= :heading (:type first-node))
      (md/node->text first-node))))

(defn find-description [ast]
  (some->> ast
           :content
           (filter (comp #{:paragraph} :type))
           md/node->text))

(defn parse-markdown [markdown-str]
  (let [ast (md/parse markdown-str)
        hiccup (md/->hiccup (assoc md/default-hiccup-renderers
                                   :html-block (fn [_ m]
                                                 [:div "LOL ugyldig HTML!"]))
                            ast)]
    {:doc/html (replicant.string/render hiccup)
     :doc/hiccup hiccup
     :title (find-title ast)
     :description (find-description ast)}))

(comment

  ;; Neste hinder for ny Markdown-parser er inline HTML.
  ;; Vi har inline HTML i feks OLORM-5.
  ;; Det gir en feilmelding i HTML-en i stedet for rendret HTML.

  (def html-in-md
    "
<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->
")

  (-> html-in-md md/parse)


  (-> html-in-md md/parse md/->hiccup)
  ;; => [:div
  ;;     [:span.message.red
  ;;      [:strong "Unknown type: ':html-block'."]
  ;;      [:code
  ;;       "{:type :html-block, :content [{:type :text, :text \"<!-- 1. Hva gjør du akkurat nå? -->\"}]}"]]
  ;;     [:span.message.red
  ;;      [:strong "Unknown type: ':html-block'."]
  ;;      [:code
  ;;       "{:type :html-block, :content [{:type :text, :text \"<!-- 2. Finner du kvalitet i det? -->\"}]}"]]
  ;;     [:span.message.red
  ;;      [:strong "Unknown type: ':html-block'."]
  ;;      [:code
  ;;       "{:type :html-block, :content [{:type :text, :text \"<!-- 3. Hvorfor / hvorfor ikke? -->\"}]}"]]]


  )
