(ns li.laratel.core
  (:require
   [clojure.java.io :as io]
   [cpath-clj.core :as cpath]
   [li.laratel.cv :as cv]
   [li.laratel.home :as home]
   [li.laratel.lowering :as lowering]
   [li.laratel.pages :refer [blog blog-post blog-posts]]
   [org.httpkit.server :as http]
   [reitit.ring :as ring]
   [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
   [ring.util.response :as response]
   [taoensso.timbre :refer [error info]])
  (:gen-class))

;; Graal does not support reflection calls
(set! *warn-on-reflection* true)

(defn mastodon-data [_]
  (response/response {:subject "acct:luciano@parens.social",
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

(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (error {:what :uncaught-exception
             :exception ex
             :where (str "Uncaught exception on" (.getName thread))}))))

(def handler
  (ring/ring-handler
   (ring/router
    [["/"
      ["" {:get home/home}]

      [".well-known/webfinger" {:get mastodon-data}]

      ["blog"
       ["" {:get blog}]
       ["/:blog-post-id" {:get blog-post
                          :parameters {:path {:blog-post-id string?}}}]]

      ["cv" {:get cv/cv}]]])
   (ring/routes
     ;; Handle trailing slash in routes - add it + redirect to it
     ;; https://github.com/metosin/reitit/blob/master/doc/ring/slash_handler.md
    (ring/redirect-trailing-slash-handler)
    (ring/create-resource-handler {:path "/"})

    (ring/create-default-handler
     {:not-found
      (constantly (-> {:status 404,
                       :body "Page not found"}
                      (response/content-type "text/html")))
      :method-not-allowed
      (constantly (-> {:status 405,
                       :body "Not allowed"}
                      (response/content-type "text/html")))
      :not-acceptable
      (constantly (-> {:status 406,
                       :body "Not acceptable"}
                      (response/content-type "text/html")))}))))

(defonce server (atom nil))

(defn -main []
  (reset! blog-posts (doall
                      (for [[_ uris] (cpath/resources (clojure.java.io/resource "blog/"))
                            :let [uri ^java.net.URI (first uris)]]
                        (with-open [in (clojure.java.io/input-stream uri)]
                          (reset! lowering/footnote-count-for-post 1)
                          (info "Parsing" (.toString uri))
                          (lowering/parse (slurp in))))))

  (reset! server (http/run-server
                  (wrap-defaults
                   handler
                   (assoc api-defaults :static {:resources "public"}))
                  {:port 3000})))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn restart! []
  (@server :timeout 100)
  (reset! server nil)
  (-main))
