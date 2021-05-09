(ns laratelli.pages.about
  (:require
   ["@dracula/dracula-ui" :as drac]))

(defn about
  "About page"
  []
  ;; TODO: use a card here.
  [:div {:class "test-class"}
   [:> drac/Paragraph
    "Luciano Laratelli is a software developer, dog parent, and all-around regular guy living in "
    [:> drac/Text {:color "yellowPink"} "Miami Beach"] ", Florida."]])
