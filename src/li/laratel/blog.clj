(ns li.laratel.blog
  (:require
   [li.laratel.util :refer [site-page table-row]]))

(def blog-posts (atom {}))

(defn blog-page [_]
  (when (empty? @blog-posts) (throw (Throwable. "Empty blog posts")))

  (let [posts-by-date (update-vals (group-by :date-int @blog-posts) first)
        ordered-dates (reverse (sort (keys posts-by-date)))]

    (site-page
     {:title "Luciano Laratelli's Blog"
      :description "Listing of Luciano Laratelli's blog posts"
      :has-code? nil}
     [:table.listing-table

      (for [date ordered-dates]
        (let [{:keys [date-str title blog-post-id]} (get posts-by-date date)]
          [:tr.listing-row
           [:td.listing-row.pad-right date-str]
           [:td.listing-row.pad-left
            [:a {:href (str "/blog/" blog-post-id)} title]]]))])))

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
