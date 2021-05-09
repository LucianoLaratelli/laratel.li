(ns laratelli.components.header
  (:require
   ["@dracula/dracula-ui" :as drac]))

(def header
  [:> drac/Heading
   {:size "xl"
    :color "purpleCyan"
    :style {:text-align "center" :padding "none"}
    :class "mainTitleDoNotPad"}
   "Luciano Laratelli"])

