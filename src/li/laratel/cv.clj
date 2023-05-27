(ns li.laratel.cv
  (:require
   [clojure.java.io :as io]
   [li.laratel.lowering :as lowering]
   [li.laratel.util :refer [site-page]]))

(defn cv [_]
  (let [{:keys [body title date-str description]} (lowering/parse (slurp (io/resource "cv.md")))]
    (site-page {:title "CV"
                :description "Luciano Laratelli's Curriculum Vitae"
                :has-code? nil}
               [:div
                [:h2 title]
                [:h4 "Last updated: " date-str]
                [:p description]
                body])))
