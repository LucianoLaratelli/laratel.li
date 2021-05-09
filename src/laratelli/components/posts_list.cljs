(ns laratelli.components.posts-list
  (:require
   ["@dracula/dracula-ui" :as drac]))

(defn get-list-item-anchor
  "Makes a list item out of a post that links to the post's page."
  [item]
  (let [id (key item)
        meta (val item)
        title (:title meta)
        date (:datestr meta)
        href (:href meta)]
    [:li
     {:class ["drac-text" "drac-text-white"]
      :key id
      :value (str date)
      :style {:padding-bottom "1.5em"}}
     [:> drac/Anchor {:href href
                      :color "white"
                      :hoverColor "yellowPink"} title]]))

(defn posts-list [posts]
  [:div
   {:class "test-class"}
   [:> drac/List
    {:class "unordered"}
    (map get-list-item-anchor posts)]])
