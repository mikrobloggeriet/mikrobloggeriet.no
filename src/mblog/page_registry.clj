(ns mblog.page-registry
  (:require
   [mblog.content-design :as content-design]
   [mblog.ui.doc :as ui.doc]))

(defn define-page [page]
  (when (not (:page/render page))
    (throw (ex-info "Invalid page: :page/render function not set." {:page page})))
  page)

(def registry
  {

   :page-registry/doc
   (define-page
     {:page/prepare-data #'ui.doc/req->innhold
      :page/render #'ui.doc/innhold->hiccup})

   :page-registry/content-design
   (define-page
     {:page/prepare-data #'content-design/req->innhold
      :page/render #'content-design/innhold->hiccup})

   })
