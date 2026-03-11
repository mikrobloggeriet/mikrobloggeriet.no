(ns mblog.http
  (:require
   [clojure.string :as str]
   [mblog.env :as env]
   [ring.middleware.cookies :as cookies]))

(defn permanent-redirect
  "Permanent redirect to target"
  [{:keys [target]}]
  (assert target)
  {:status 308
   :headers {"Location" target}})

(defn response-ok? [response]
  (= 200 (:status response)))

(defn path-param
  "Get a path parameter from an HTTP request."
  [req param]
  (get-in req [:path-params param]))

(defn set-session [req session-id]
  {:status 307
   :headers {"Location" (:uri req)
             "Set-Cookie" (str "session_id=" session-id "; Path=/; Max-Age=31556952; HttpOnly")}
   :body ""})

(defn find-session [req]
  (get-in (cookies/cookies-request req) [:cookies "session_id" :value]))

(defn wrap-ensure-session [handler]
  (fn [req]
    (let [session-id (find-session req)]
      (cond session-id
            (-> req
                (assoc :session/id session-id)
                handler)

            (not= :get (:request-method handler))
            (handler req)

            :else
            (set-session req (random-uuid))))))

(defn wrap-block-laboratoriet
  "Block requests to /laboratoriet/... in production."
  [handler]
  (fn [req]
    (if (and (env/prod?) (str/starts-with? (:uri req) "/laboratoriet"))
      {:status 404 :body "Not found" :headers {"Content-Type" "text/plain"}}
      (handler req))))
