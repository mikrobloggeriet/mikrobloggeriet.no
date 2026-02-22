(ns mblog.chat
  (:require [hiccup.page :as page]))

(defn req->innhold [_req]
  {})

(defn innhold->hiccup [_innhold]
  (page/html5
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title "Chat"]
    [:link {:rel "stylesheet" :href "/css/chat.css"}]]
   [:body
    [:main
     [:chat-message
      [:header [:strong "Alice"] [:time "09:41"]]
      [:p "Hey, is anyone here?"]]
     [:chat-message
      [:header [:strong "Bob"] [:time "09:42"]]
      [:p "Yeah, just got online. What's up?"]]
     [:chat-message
      [:header [:strong "Alice"] [:time "09:43"]]
      [:p "Not much, just testing this chat thing out."]]
     [:chat-message
      [:header [:strong "Charlie"] [:time "09:45"]]
      [:p "Looks like it works!"]]]
    [:form
     [:input {:type "text" :placeholder "Type a message…" :required true}]
     [:button {:type "submit"} "Send"]]]))
