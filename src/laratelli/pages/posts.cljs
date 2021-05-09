(ns laratelli.pages.posts
  (:require
   [laratelli.components.posts-list :refer [posts-list]]
   [laratelli.global-state :as global-state]))

(defn posts
  "List of post titles."
  []
  (posts-list @global-state/post-data))
