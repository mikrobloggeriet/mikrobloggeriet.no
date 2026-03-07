(ns mblog.page-registry
  (:require
   [mblog.content-design :as content-design]
   [mblog.indigo :as indigo]
   [mblog.ui.doc :as ui.doc]))

(defn define-page [page]
  (when (not (:page/render page))
    (throw (ex-info "Invalid page: :page/render function not set." {:page page})))
  page)

(def registry
  {:page-registry/indigo
   (define-page
     {:page/prepare-data #'indigo/req->innhold
      :page/render #'indigo/innhold->hiccup})

   :page-registry/doc
   (define-page
     {:page/prepare-data #'ui.doc/req->innhold
      :page/render #'ui.doc/innhold->hiccup})

   :page-registry/content-design
   (define-page
     {:page/prepare-data #'content-design/req->innhold
      :page/render #'content-design/innhold->hiccup})

   })
