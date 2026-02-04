(ns ^:clj-reload/no-reload user
  (:require
   [clj-reload.core :refer [reload]]
   [clojure.repl.deps :refer [sync-deps]]))

;; Anbefalt måte å starte opp Mikrobloggeriet er:
;;
;; 1. Kjør `garden run`
;; 2. Koble til REPL fra din editor.

;; Da får du et lokalt miljø som er kliss likt prod. Hvis du alikevel ønsker å
;; kjøre koden herfra, kan du bruke `start!` under.

(defn ^:export start!
  []
  ((requiring-resolve 'mblog.system/start!) {}))

(comment
  (reload)
  mblog.state/datomic
  (sync-deps))
