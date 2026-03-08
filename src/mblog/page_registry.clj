(ns mblog.page-registry
  (:require
   [mblog.content-design :as content-design]
   [mblog.ui.doc :as ui.doc]
   [mblog.ui.read :as ui.read]))

(defn define-page [page]
  (when (not (:page/data->hiccup page))
    (throw (ex-info "Invalid page: :page/render function not set." {:page page})))
  page)

(def registry
  {

   :page-registry/doc
   (define-page
     {:page/parse-request #'ui.doc/parse-request
      :page/request->data #'ui.doc/req->innhold
      :page/data->hiccup #'ui.doc/innhold->hiccup})

   :page-registry/read
   (define-page
     {:page/request->data #'ui.read/req->innhold
      :page/data->hiccup #'ui.read/innhold->hiccup})

   :page-registry/content-design
   (define-page
     {:page/request->data #'content-design/req->innhold
      :page/data->hiccup #'content-design/innhold->hiccup})

   })

(comment
  (def todo (constantly nil))
  {:page/parse-request #'todo
   :page/request->data #'todo
   :page/data->hiccup #'todo}

  )
