(ns li.laratel.pages
  (:require
   [clojure.java.io :as io]
   [li.laratel.lowering :as lowering]
   [li.laratel.util :refer [pars route-data table-row site-page]]))

(def blog-posts (atom {}))

(defn home [_]
  (site-page
   {:title "Luciano Laratelli"
    :description "Home page of Luciano Laratelli's personal website. Contains a
    short bio and various contacts methods, including
    email (luciano@laratel.li), links to GitHub and SourceHut profiles,
    LinkedIn, PGP key, and my parens.social mastodon instance username."
    :has-code? false}
   (pars
    "Hello and welcome!"
    "This is the personal website for me, Luciano Laratelli."
    "I'm a software developer based out of Miami Beach. My technical interests
    include functional programming, especially in Clojure, as well as herding
    Linux servers. I'm an Emacs enthusiast. Before I got into computers, I spent
    far too much time thinking about chemistry."
    ["You can reach me by email at " [:a {:href "mailto:luciano@laratel.li"} "luciano@laratel.li"] "."]
    ["I publish open-source projects on both "
     [:a {:href "https://git.sr.ht/~luciano/"} "SourceHut"]
     " and "
     [:a {:href "https://github.com/LucianoLaratelli/"} "GitHub"] "."]

    ["I'm unfortunately on " [:a {:href "https://www.linkedin.com/in/luciano-laratelli-663851a1/"} "LinkedIn"] "."]

    ["Interested in PGP? I'm on " [:a {:href "https://keybase.io/lucianolaratelli"} "Keybase"] ". "
     "You can also " [:a {:href "/public-key.asc"} "view"] " my public key."]
    [[:a
      {:href "https://parens.social/@luciano"
       :rel "me"}
      "Let's federate!"]]

    ["This site is written in Clojure; its source code lives "
     [:a {:href "https://github.com/LucianoLaratelli/laratel.li"} "on my GitHub"]
     ". I used " [:a
                  {:href "https://neat.joeldare.com/"} "neat.css"]
     ", with some modifications. Blog post parsing made possible thanks to the
     wonderful "
     [:a {:href "https://github.com/kiranshila/cybermonday"} "cybermonday"] " library."])))

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

(defn blog-post [{{:keys [blog-post-id]} :path-params
                  :as request}]
  (let [{:keys [body title description date-str]}
        (->> (route-data request)
             :posts
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

(defn cv [_]
  (let [{:keys [body title date-str description]} (lowering/parse (slurp (io/resource "cv.md")))]
    (site-page {:title "CV"
                :description "Luciano Laratelli's Curriculum Vitae"
                :has-code? nil}
               [:div
                [:h2 title]
                [:h4 "Last updated: " date-str]
                [:p description]
                body])))
