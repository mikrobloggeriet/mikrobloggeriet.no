(ns mblog.theme-test
  (:require
   [mblog.theme :as theme]
   [clojure.test :refer [deftest testing is]]))

(def white [255 255 255])
(def black [0 0 0])

(deftest generate-colors
  (is (= (theme/generate-colors {:bg-color black :text-color white})
         {:bg-color black :text-color white}))

  (is (contains? (theme/generate-colors {:bg-color white})
                 :text-color))
  (is (contains? (theme/generate-colors {:text-color black})
                 :bg-color)))

(deftest generate
  (is (theme/generate {:font "font1.css", :bg-color [104 101 32]})))

(deftest get-locked
  (testing "Get the locked values from a theme-state"
    (is (= (theme/get-locked {:theme {:bg-color [255 255 255]
                                      :text-color [0 0 255]
                                      :font "\"Noto Serif\", serif"}
                              :locked #{:font}})
           {:font "\"Noto Serif\", serif"}))))

(deftest update-and-get*
  (let [store (atom {"SESSION"
                     {:theme {:bg-color [255 255 255]
                              :text-color [0 0 255]
                              :font "\"Noto Serif\", serif"}
                      :locked #{:font}}})]
    (is (= (:font (:theme (theme/update-and-get* store "SESSION")))
           "\"Noto Serif\", serif"))
    (is (= (:locked (theme/update-and-get* store "SESSION"))
           #{:font}))))


