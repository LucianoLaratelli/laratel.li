(ns laratelli.pages.blog-post
  (:require
   ["@dracula/dracula-ui" :as drac]
   [cybermonday.lowering :as lower]
   [cybermonday.utils :as cmu]
   [laratelli.global-state :as global-state]
   [laratelli.lowering :refer [default-attrs lower-fns]]
   [reitit.frontend.easy :as rfe]))

(defn back-to-posts
  [ref]
  [:> drac/Button
   {:size "xs"
    :variant "ghost"
    :as "a"
    :href (rfe/href ref)}
   "<- to posts"])

(defn get-top-header-text [md]
  (let [headers
        (->> md
             (filter seqable?)
             (filter #(and (= (first %) :markdown/heading)
                           (= (:level (second %)) 1))))]
    (map #(nth % 2) headers)))

(defn blog-post
  "An actual blog post."
  [match]
  (let [id (get-in match [:parameters :path :id])
        ref (get-in match [:data :ref])
        post-info ((keyword (str id)) @global-state/post-data)
        title (:title post-info)
        date (:datestr post-info)
        md (:md post-info)
        ;; TODO: use this later to make a jump bar to headers
        ;; headers (get-top-header-text md)
        ]
    [:div [:> drac/Heading {:size "xl"} title]
     [:> drac/Heading {:size "md"} date]
     (back-to-posts ref)
     [:> drac/Divider {:color "purple"}]
     (cmu/cleanup
      (lower/to-html-hiccup md {:lower-fns lower-fns :default-attrs default-attrs}))
     [:> drac/Divider {:color "purple"}]]))
