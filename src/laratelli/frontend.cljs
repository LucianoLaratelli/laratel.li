(ns laratelli.frontend
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.coercion.spec :as rss]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]
   [laratelli.pages :as pages]
   [clojure.string :as s]
   ["@dracula/dracula-ui" :as drac]))

(def header
  [:> drac/Heading
   {:size "xl"
    :color "purpleCyan"
    :style {:text-align "center" :padding "none"}
    :class "mainTitleDoNotPad"}
   "Luciano Laratelli"])

(defonce match (r/atom nil))

(def tab-class
  {:active "drac-tab drac-tab-active"
   :inactive "drac-tab"})

(def tab-colors
  {"home" "purple"
   "about" "red"
   "contact" "yellow"
   "cv" "green"
   "posts" "cyan"})

(def tab-names
  ["home" "about" "contact" "cv" "posts"])

(defn one-tab [tab active]
  (let [class (active tab-class)]
    (vector :li {:class class}
            [:> drac/Anchor {:class "drac-tab-link drac-text"
                             :href (rfe/href (get pages/refs (keyword tab)))
                             :style {:padding-left ".75em" :padding-right ".75em"}}
             tab])))

(defn make-tabs [tabs active-tab active-tab-color]
  (into [:> drac/Tabs
         {:color active-tab-color}]
        (map #(one-tab % (if (= % active-tab)
                           :active
                           :inactive))
             tabs)))

(defn current-page []
  [:> drac/Box {:width "auto"}
   (if (not (= "id" (last (s/split (:template @match) ":"))))
     [:div header
      (let [tab-name (name (:name (:data @match)))
            tab-color (get tab-colors tab-name)]
        (make-tabs tab-names tab-name tab-color))]
     (js/console.log "No tab bar needed"))

   (when @match
     (let [view (:view (:data @match))]
       [view @match]))])

(defn main []
  (rfe/start!
   (rf/router pages/routes {:data {:coercion rss/coercion}})
   (fn [m] (do (reset! match m)
               (js/console.log match)))
   {:use-fragment true})
  (reagent.dom/render [current-page] (.getElementById js/document "app")))
