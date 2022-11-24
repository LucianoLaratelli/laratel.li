(ns li.laratel.luciano.env
  (:require
    [clojure.tools.logging :as log]
    [li.laratel.luciano.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[ starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[ started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[ has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev
                :persist-data? true}})
