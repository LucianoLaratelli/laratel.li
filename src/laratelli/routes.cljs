(ns laratelli.routes
  (:require
   [laratelli.pages.blog-post :refer [blog-post]]
   [laratelli.pages.contact :refer [contact]]
   [laratelli.pages.cv :refer [cv]]
   [laratelli.pages.home :refer [home]]
   [laratelli.pages.posts :refer [posts]]))

(def refs
  "References to the pages on my site. They're used when building the tabs, but
  because the pages are routes, this def has to be in this file. I think."
  {:home ::home
   :contact ::contact
   :cv ::cv
   :posts ::posts
   :post ::post})

(def routes
  [["/"
    {:name ::home
     :view home}]
   ["/contact"
    {:name ::contact
     :view contact}]
   ["/cv"
    {:name ::cv
     :view cv}]
   ["/posts"
    [""
     {:name ::posts
      :view posts}]
    ["/:id"
     {:name ::post
      :view blog-post
      :parameters {:path {:id string?}}
      :posts-page ::posts}]]])
