(ns mblog.http
  (:require
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
    (if (find-session req)
      (handler req)
      (set-session req (random-uuid)))))
