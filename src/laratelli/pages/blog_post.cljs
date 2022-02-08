(ns laratelli.pages.blog-post
  (:require
   ["@dracula/dracula-ui" :as drac]
   [cybermonday.lowering :as lower]
   [cybermonday.utils :as cmu]
   [laratelli.global-state :as global-state]
   [laratelli.lowering :refer [default-attrs lower-fns]]
   [reitit.frontend.easy :as rfe]))

(defn post-link
  [ref title]
  [:> drac/Button
   (merge {:size "xs"
           :as "a"
           :variant "outline"
           :style {:margin-right "1em"}}
          (if (nil? ref)
            {:disabled "{true}"
             :color "blackSecondary"}
            {:href ref}))
   (case title
     :next "Next Post"
     :prev "Previous Post"
     :back "All Posts")])

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
        posts-page (get-in match [:data :posts-page])
        post-info ((keyword (str id)) @global-state/post-data)
        title (:title post-info)
        date (:datestr post-info)
        md (:md post-info)
        links (get @global-state/post-links id)
        prev (:prev links)
        next (:next links)
        ;; TODO: use this later to make a jump bar to headers
        ;; headers (get-top-header-text md)
        ]
    [:div [:> drac/Heading {:size "xl"} title]
     [:> drac/Heading {:size "md"} date]
     (post-link (rfe/href posts-page) :back)
     (post-link prev :prev)
     (post-link next :next)
     [:> drac/Divider {:color "purple"}]
     (cmu/cleanup
      (lower/to-html-hiccup md {:lower-fns lower-fns :default-attrs default-attrs}))
     [:> drac/Divider {:color "purple"}]]))
