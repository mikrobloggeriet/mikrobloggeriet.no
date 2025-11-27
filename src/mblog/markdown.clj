(ns mblog.markdown
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

(defn parse [s]
  (let [ast (md/parse s)
        hiccup (md/->hiccup (assoc md/default-hiccup-renderers
                                   :html-block (fn [_ m]
                                                 [:div "LOL ugyldig HTML!"]))
                            ast)]
    {:doc/html (replicant.string/render hiccup)
     :doc/hiccup hiccup
     :title (find-title ast)
     :description (find-description ast)}))

(comment
  ;; We need allow inline HTML to pass through - several post rely on just that.

  (def html-in-md
    "
# Morgenstund har gull i munn

Heisann!

<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->
")

  (md/parse html-in-md)

  (parse html-in-md)

  )
