(ns laratelli.pages.home
  (:require
   ["@dracula/dracula-ui" :as drac]
   [laratelli.components.posts-list :refer [posts-list]]
   [laratelli.global-state :as global-state]))

(defn home
  "Home page."
  []
  [:div
   [:> drac/Paragraph
    "Hi, welcome to my site."]
   [:> drac/Heading
    {:size "L"
     :color "white"}
    "Recent blog posts:"]
   (posts-list (take 3 @global-state/post-data))])
