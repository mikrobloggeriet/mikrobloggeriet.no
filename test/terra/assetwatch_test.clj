(ns terra.assetwatch-test
  (:require [clojure.test :refer [deftest is]]
            [terra.assetwatch :as assetwatch]))

(deftest handler
  (is (assetwatch/handler {:request-method :get :uri "/js/datastar.js"}))
  (is (assetwatch/handler {:request-method :get :uri "/vanilla.css"}))

  )
