(ns laratelli.components.cv
  (:require
   ["@dracula/dracula-ui" :as drac]))

(defn skills-list [title skills]
  [:div
   [:> drac/Heading {:size "L"} title]
   [:> drac/List
    (map #(vector :li {:className "drac-text drac-text-white" :key %} %) skills)]])

(defn employment-badges [[role time-span location]]
  (let [style {:margin-left "0em"
               :padding-left 6
               :padding-right 6
               :padding-top 2
               :padding-bottom 2}]

    [:> drac/List {:style {:padding-left 0
                           :margin -1}}

     [:li {:style {:margin-top ".5em"
                   :margin-bottom ".5em"}}
      [:> drac/Badge {:variant "outline" :color "green"
                      :m "xs" :style style}
       role]]
     [:li
      {:style {:margin-top ".5em"
               :margin-bottom ".5em"}}
      [:> drac/Badge {:variant "subtle" :color "orange"
                      :m "xs" :style style}
       time-span]]
     [:li
      {:style {:margin-top ".5em"
               :margin-bottom ".5em"}}
      [:> drac/Badge {:variant "normal" :color "purpleCyan"
                      :m "xs" :style style}
       location]]]))

(defn other-work [employer [role time-span location] description]
  [:div
   {:style {:padding-bottom "1em"}}
   [:> drac/Heading {:size "l"}
    employer]
   (employment-badges [role
                       time-span
                       location])
   [:> drac/Text
    description]])

(defn awards [title description]
  [:div {:style {:padding-bottom "1em"}}
   [:> drac/Heading {:size "l"}
    title]
   [:> drac/Text description]])

(defn education-heading [degree [institution graduation-date location]]
  [:div {:style {:padding-bottom "1em"}}
   [:> drac/Heading {:size "l"}
    degree]
   (employment-badges [institution
                       graduation-date
                       location])])
