(ns laratelli.frontend
  "Frontend for laratel.li. Main dispatches based on the routes in laratelli.routes.
  Routes reads from the page components in laratelli.pages to actually build the site.
  Blog posts are markdown stored in resources/blog.
  "
  (:require ["@dracula/dracula-ui" :as drac]
            [clojure.string :as s]
            [laratelli.components.header :as header]
            [laratelli.components.tabs :as tabs]
            [laratelli.global-state :as global-state]
            [laratelli.routes :as routes]
            [reagent.dom :as rdom]
            [reitit.coercion.spec :as rss]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe])
  (:require-macros [laratelli.parsed-posts :refer [parsed-posts]]))

(defn get-links [posts]
  (map-indexed
   (fn [idx _]
     (cond
       (= idx 0)
       {:next (rfe/href (get routes/refs :post) {:id (:id (nth posts (+ idx 1)))})}
       (= idx (- (count posts) 1))
       {:prev (rfe/href (get routes/refs :post) {:id (:id (nth posts (- idx 1)))})}
       :else
       {:prev (rfe/href (get routes/refs :post) {:id (:id (nth posts (- idx 1)))})
        :next (rfe/href (get routes/refs :post) {:id (:id (nth posts (+ idx 1)))})}))
   posts))

(defn current-page
  "I only want pages that aren't blog posts to show the tab bar; blog posts omit
  it to highlight the post title and add a `back to posts` button in the same
  space. Current-page intercepts the match to decide that."
  []

  ;I only want this to run once, but if it's in main it makes development a
  ;pain. I guess I could setup a stop! or something but I went with this
  ;instead.
  (when (not @global-state/didInitRoutes)
    ;Attach an href to each post. This gets used when displaying post metadata
    ;outside of a blog_post page, so that I can link to the post.
    (reset! global-state/post-data
            (into {}
                  (map (fn [[k v]]
                         (assoc {} k
                                (assoc v :href (rfe/href (get routes/refs :post) {:id (:id v)}))))
                       (parsed-posts))))

    (reset! global-state/post-links (get-links (vals @global-state/post-data)))

    (reset! global-state/didInitRoutes true))

  [:> drac/Box {:width "auto"}
   (when (not (= "id" (last (s/split (:template @global-state/match) ":"))))
     [:div
      header/header
      (let [tab-name (name (:name (:data @global-state/match)))]
        (tabs/make-tabs tab-name))])

   (when @global-state/match
     (let [view (:view (:data @global-state/match))]
       [view @global-state/match]))])

(defn main []
  (rfe/start!
   (rf/router routes/routes {:data {:coercion rss/coercion}})
   (fn [m] (reset! global-state/match m))
   {:use-fragment true})
  (reagent.dom/render [current-page] (.getElementById js/document "app")))
