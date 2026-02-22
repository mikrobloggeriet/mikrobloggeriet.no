(ns mblog.page-registry
  (:require
   [mblog.chat :as chat]
   [mblog.content-design :as content-design]
   [mblog.indigo :as indigo]
   [mblog.ui.doc :as ui.doc]))

(defn define-page [page]
  (when (not (:pagemaker/render page))
    (throw (ex-info "Invalid page: :page/render function not set." {:page page})))
  page)

(def registry
  {:page/indigo
   (define-page
     {:pagemaker/prepare-data #'indigo/req->innhold
      :pagemaker/render #'indigo/innhold->hiccup})

   :page/doc
   (define-page
     {:pagemaker/prepare-data #'ui.doc/req->innhold
      :pagemaker/render #'ui.doc/doc->hiccup})

   :page/content-design
   (define-page
     {:pagemaker/prepare-data #'content-design/req->innhold
      :pagemaker/render #'content-design/innhold->hiccup})

   :page/chat
   (define-page
     {:pagemaker/prepare-data #'chat/req->innhold
      :pagemaker/render #'chat/innhold->hiccup})})
