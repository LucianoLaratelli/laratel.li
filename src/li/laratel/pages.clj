(ns li.laratel.pages
  (:require
   [li.laratel.util :refer [site-page table-row]]))

(def blog-posts (atom {}))

(defn blog [_]
  (when (empty? @blog-posts) (throw (Throwable. "Empty blog posts")))

  (let [posts-by-date (update-vals (group-by :date-int @blog-posts) first)
        ordered-dates (reverse (sort (keys posts-by-date)))]

    (site-page
     {:title "Luciano Laratelli's Blog"
      :description "Listing of Luciano Laratelli's blog posts"
      :has-code? nil}
     [:p
      [:table {:style
               {:border-collapse "collapse"}}

       (for [date ordered-dates]
         (let [{:keys [date-str title blog-post-id]} (get posts-by-date date)]
           [:tr
            (table-row date-str)
            (table-row
             [:a {:href (str "/blog/" blog-post-id)} title])]))]])))

(defn blog-post [{{:keys [blog-post-id]} :path-params}]
  (let [{:keys [body title description date-str]}
        (->> @blog-posts
             (filter (fn [post]
                       (= (:blog-post-id post)
                          blog-post-id)))
             first)]

    (site-page {:title title
                :description description
                :has-code? true}
               [:div
                [:h2 title]
                [:h4 date-str]
                [:p description]
                [:hr]
                body])))
