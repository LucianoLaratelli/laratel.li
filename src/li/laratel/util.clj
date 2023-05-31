(ns li.laratel.util
  (:require
   [hiccup.page :as p]
   [ring.util.response :as response]))

(defn page [opts & content]
  (-> (p/html5 opts content)
      response/response
      (response/content-type "text/html")))

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
             :href "/style/pygments-styling.min.css"}])

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
    ;; [:a.in-nav {:href "/programs"} "Programs"]
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

(defn table-row [content]
  [:td {:style {:vertical-align "top"
                :height "0"}}
   content])
