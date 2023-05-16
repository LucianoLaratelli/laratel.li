(ns li.laratel.core
  (:require
   [clojure.java.io :as io]
   [cpath-clj.core :as cpath]
   [li.laratel.lowering :as lowering]
   [li.laratel.pages :refer [blog blog-post blog-posts cv home]]
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
      ["" {:get home}]

      [".well-known/webfinger" {:get mastodon-data}]

      ["blog"
       ["" {:get blog}]
       ["/:blog-post-id" {:get blog-post
                          :parameters {:path {:blog-post-id string?}}}]]

      ["cv" {:get cv}]]])
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

(defmethod response/resource-data :resource
  [^java.net.URL url]
  ;; GraalVM resource scheme
  (let [resource (.openConnection url)
        len (#'ring.util.response/connection-content-length resource)]
    (when (pos? len)
      {:content        (.getInputStream resource)
       :content-length len
       :last-modified  (#'ring.util.response/connection-last-modified resource)})))

(defn -main []
  (reset! blog-posts (doall
                      (for [[_ uris] (cpath/resources (clojure.java.io/resource "blog/"))
                            :let [uri ^java.net.URI (first uris)]]
                        (with-open [in (clojure.java.io/input-stream uri)]
                          (reset! lowering/footnote-count-for-post 1)
                          (lowering/parse (slurp in))
                          (info "Parsed" (.toString uri))))))

  (http/run-server
   (wrap-defaults
    handler
    (assoc api-defaults :static {:resources "public"}))
   {:port 3000}))
