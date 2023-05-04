(ns li.laratel.luciano.web.routes.ui
  (:require
   [clojure.java.io :as io]
   [clojure.pprint :as pp]
   [clojure.tools.logging :as log]
   [cpath-clj.core :as cpath]
   [integrant.core :as ig]
   [li.laratel.luciano.web.htmx :refer [page] :as htmx]
   [li.laratel.luciano.web.middleware.exception :as exception]
   [li.laratel.luciano.web.middleware.formats :as formats]
   [li.laratel.luciano.web.routes.lowering :as lowering]
   [li.laratel.luciano.web.routes.utils :as utils]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.util.http-response :as http-response]))

(defn site-head [title description has-code?]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0"}]

   [:meta {:name "author"
           :content "Luciano Laratelli"}]

   [:meta {:name "description"
           :content description}]

   [:title title]

   (when has-code?
     [:link {:rel "stylesheet"
             :href "/style/gruvbox.min.css"}])
   (when has-code?
     [:script {:src "/style/highlight.min.js"}])
   (when has-code?
     [:script "hljs.highlightAll();"])

   [:link {:rel "stylesheet"
           :href "/style/neat.min.css"}]
   [:link {:rel "stylesheet"
           :href "/style/custom.min.css"}]
   [:script
    {:defer "",
     :data-domain "luciano.laratel.li",
     :src "https://plausible.io/js/script.js"}]])

(defn pars [& paragraphs]
  (apply vector :div (map (fn [par]
                            (if (vector? par)
                              (apply (partial vector :p) par)
                              (vector :p par)))
                          paragraphs)))

(defn header []
  [:div
   [:h1 "Luciano Laratelli"]
   [:nav
    [:a.in-nav {:href "/"} "Home"]
    [:a.in-nav {:href "/blog"} "Blog"]
    [:a.in-nav {:href "/cv"} "CV"]
    ;; [:a.in-nav {:href "/home-cooked"} "Programs"]
    ;; [:a.in-nav {:href "/projects"} "Projects"]
    ]])

(defn site-page
  "Wrapper for page to spread shared style everywhere.
  `has-code?` tells us a page has source code on it, which should be higlighted.
  We have to include the stylesheets for that, but we don't want to do that on
  every page as its wasteful."
  [{:keys [title description has-code?]} & body]
  (page

   (site-head title description has-code?)

   (header)

   (apply vector :body body)))

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

(defn table-row [content]
  [:td {:style {:vertical-align "top"
                :height "0"}}
   content])

(defn blog [request]
  (let [{:keys [posts]} (utils/route-data request)
        posts-by-date (update-vals (group-by :date-int posts) first)
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
        (->> (utils/route-data request)
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

(defn print-and-return [guh]
  (pp/pprint guh)
  guh)

(defn mastodon-data [_]
  (http-response/ok {:subject "acct:luciano@parens.social",
                     :aliases ["https://parens.social/@luciano"
                               "https://parens.social/users/luciano"],
                     :links
                     [{:rel "http://webfinger.net/rel/profile-page",
                       :type "text/html",
                       :href "https://parens.social/@luciano"}
                      {:rel "self",
                       :type "application/activity+json",
                       :href "https://parens.social/users/luciano"}
                      {:rel "http://ostatus.org/schema/1.0/subscribe",
                       :template "https://parens.social/authorize_interaction?uri={uri}"}]}))

;; Routes
(defn ui-routes [_opts]
  [["/"
    ["" {:get home}]

    [".well-known/webfinger" {:get mastodon-data}]

    ["blog"
     ["" {:get blog}]
     ["/:blog-post-id" {:get blog-post
                        :parameters {:path {:blog-post-id string?}}}]]

    ["cv" {:get cv}]]])

(defn route-data [opts]
  (merge
   opts
   {:muuntaja   formats/instance
    :middleware
    [;; Default middleware for ui
     ;; query-params & form-params
     parameters/parameters-middleware
     ;; encoding response body
     muuntaja/format-response-middleware
     ;; exception handling
     exception/wrap-exception]}))

(derive :reitit.routes/ui :reitit/routes)

(defmethod ig/init-key :reitit.routes/ui
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path (route-data opts) (ui-routes opts)])

(defmethod ig/init-key :blog/posts
  [_ _]
  (for [[_ uris] (cpath/resources (clojure.java.io/resource "blog/"))
        :let [uri (first uris)]]
    (with-open [in (clojure.java.io/input-stream uri)]
      (->> uri
           .toString
           (log/info "Parsing"))
      (reset! lowering/footnote-count-for-post 1)
      (lowering/parse (slurp in)))))
