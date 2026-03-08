(ns mblog.page-registry
  (:require
   [mblog.content-design :as content-design]
   [mblog.ui.doc :as ui.doc]
   [mblog.ui.read :as ui.read]))

(defn define-page [page]
  (when (not (:page/innhold->hiccup page))
    (throw (ex-info "Invalid page: :page/render function not set." {:page page})))
  page)

(def registry
  {

   :page-registry/doc
   (define-page
     {:page/parse-request #'ui.doc/parse-request
      :page/req->innhold #'ui.doc/req->innhold
      :page/innhold->hiccup #'ui.doc/innhold->hiccup})

   :page-registry/read
   (define-page
     {:page/req->innhold #'ui.read/req->innhold
      :page/innhold->hiccup #'ui.read/innhold->hiccup})

   :page-registry/content-design
   (define-page
     {:page/req->innhold #'content-design/req->innhold
      :page/innhold->hiccup #'content-design/innhold->hiccup})

   })
