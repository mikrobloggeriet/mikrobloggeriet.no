(ns mblog.chat
  (:require
   [duratom.core :refer [duratom]]
   [hiccup.page :as page]))

(defonce store
  (if-let [storage-path (System/getenv "GARDEN_STORAGE")]
    (duratom :local-file
             :file-path (str storage-path "/mblog.chat.edn")
             :commit-mode :sync
             :init {})
    (atom {})))

(comment

  (swap! store empty)

  (swap! store update :messages append
         {:message/author "Alice"
          :message/timestamp "09:41"
          :message/content "Hey, is anyone here?"}
         {:message/author "Bob"
          :message/timestamp "09:42"
          :message/content "Yeah, just got online. What's up?"}
         {:message/author "Alice"
          :message/timestamp "09:43"
          :message/content "Not much, just testing this chat thing out."}
         {:message/author "Charlie"
          :message/timestamp "09:45"
          :message/content "Looks like it works!"})

  )

(defn append [xs msg & more]
  (apply (fnil conj []) xs msg more))

(defn req->innhold [_req]
  (:messages @store))

(defn innhold->hiccup [messages]
  (page/html5
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title "Chat"]
    [:link {:rel "stylesheet" :href "/css/chat.css"}]]
   [:body
    [:main
     (for [msg messages]
       [:chat-message
        [:header
         [:strong (:message/author msg)]
         [:time (:message/timestamp msg)]]
        [:p (:message/content msg)]])]
    [:form
     [:input {:type "text" :placeholder "Type a message…" :required true}]
     [:button {:type "submit"} "Send"]]]))
