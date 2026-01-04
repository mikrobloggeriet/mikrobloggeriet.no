(ns terra.assetwatch
  "Watch ONE folder and serve assets from it"
  ;; This namespace was almost named "eye" to be the "eye of the beholder".
  (:require [babashka.fs :as fs]
            [mblog.mime]
            [nextjournal.beholder :as beholder]))

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

(defonce !watcher (atom nil))
(defonce !folder (atom nil))

(defn stop [watcher]
  (beholder/stop watcher))

(defn stop! []
  (when-let [w @!watcher] (stop w))
  (reset! !watcher nil))

(defn watch!
  "Watch given folder

  Due to limited attention span, can watch only one folder. If you want to put
  your assets in many folders, consider first why you want to inflict pain upon
  yourself. Should you decide, \"yes, for I am an individual whose growth spurs
  in pain\", [consider inlining](https://play.teod.eu/consider-inlining/) the
  parts of this namespace which you desire. Then reflect upon whether spreading
  assets around your disk sparks yoy, or if it's just your pendantics speaking.

  Should you, against the advice of your docstring, attempt to watch a second
  folder, any previous folder watch attempts will be forgotten. Erased. Washed
  away by the sands of time.

  Cheers!"
  [folder]
  (when-let [w @!watcher] (stop w))
  (reset! !folder folder)
  ;; TODO aaaaachshually also watch the folder in order to do stuff
  ;;
  ;; Buuuuuut currently we don't supply any means of *getting* the watched
  ;; stuff, nor subscribing to events, of any kind. Therefore, of course, we
  ;; cheat. There is no assetwatch, yet. Only an API shim. Under which the
  ;; assetwatch may *sneakily* insert itself. In the future, under darkness,
  ;; while nobody watches.
  )

(defn handler "Serve assets from watched folder"
  [{:keys [request-method uri]}]
  (when (= :get request-method)
    (when-let [folder @!folder]
      (let [file (str folder uri)]
        (when (fs/exists? file)
          {:status 200
           :headers {"Content-Type" (mblog.mime/file->type file)}
           :body (fs/file file)})))))
