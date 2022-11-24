(ns li.laratel.luciano.web.routes.ui-test
  (:require
   [clojure.test :refer [deftest is]]
   [li.laratel.luciano.web.routes.ui :as sut]))

(deftest li.laratel.luciano.web.routes.ui-test
  (is (= [:div [:p "guh"] [:p "buh"] [:p "suh"] [:p "fuh"]]
         (sut/pars "guh" "buh" "suh" "fuh")))
  (is (= [:div [:p "guh"] [:p "buh" "fuh buh!"] [:p "suh"] [:p "fuh"]]
         (sut/pars "guh" ["buh" "fuh buh!"] "suh" "fuh"))))
