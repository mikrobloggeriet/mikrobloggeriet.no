(ns mblog.testdb
  "Shared test database - loads once, reusable across test namespaces."
  (:require [mblog.db :as db]))

(defonce !db (atom nil))

(defn reload
  "Force reload the database from disk."
  []
  (reset! !db (db/loaddb {:cohorts db/cohorts :authors db/authors})))

(defn get-instance
  "Get the shared test database, loading it if not already loaded."
  []
  (or @!db (reload)))
