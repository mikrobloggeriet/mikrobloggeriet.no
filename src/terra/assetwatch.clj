(ns terra.assetwatch
  "Watch one folder, serve assets, and notify on changes"
  (:require [babashka.fs :as fs]
            [mblog.mime]
            [nextjournal.beholder :as beholder]))

(defonce !watcher (atom nil))
(defonce !folder (atom nil))

(defn stop [watcher]
  (beholder/stop watcher))

(defn stop! []
  (when-let [w @!watcher] (stop w))
  (reset! !watcher nil))

(defn watch!
  [folder]
  (when-let [w @!watcher] (stop w))
  (reset! !folder folder))

(defn handler "Serve assets from watched folder"
  [{:keys [request-method uri]}]
  (when (= :get request-method)
    (when-let [folder @!folder]
      (let [file (str folder uri)
            file (if (fs/directory? file) (str file "index.html") file)]
        (when (fs/regular-file? file)
          {:status 200
           :headers {"Content-Type" (mblog.mime/file->type file)}
           :body (fs/file file)})))))

(comment
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; DESIGN NOTES

  ;; Help Datastar Clojurians push changed assets to the browser

  ;; = PRIMITIVES
  ;; Watcher
  ;; File path
  ;; Cache key

  ;; = DERIVED
  ;; Web path

  ;; = INTERFACE
  ;; Asset-changed hook
  ;; - File path
  ;; - Cache key
  ;; - Web path
  ;;
  ;; Ring handler
  ;; - Serve files with correct cache key as 200
  ;; - Redirect outdated assets with a redirect
  ;;   (not sure if permanent or temporary? Or moved?)

  ;; = DOM
  ;; - Add data-asset-originator to served assets
  ;; - Use data-asset-originator as morph target for changes

  ;; = DELIVERY
  ;; Step 1
  ;; Ring handler + mapping from path to cache stamped path may be delivered without weird API changes.
  ;; But we then have to stop direct-linking to non-cache-stamped paths.
  ;;
  ;; Step 2
  ;; - Need Datastar script loaded in HTML
  ;; - Need machinery for stitching together HTML / Hiccup

  ;; = PLAYING AROUND
  ;; Once we get this working, we have quite a nice JS playground!
  ;; It'll reload *really really fast*, faster than any other mechanisms I've seen, mostly due to *narrow* reloading.
  ;; Also, it keeps you keenly in control of your own HTML.
  ;; Would be quite nice to use from Babashka, actually.

  )
