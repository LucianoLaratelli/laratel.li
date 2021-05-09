(ns laratelli.contact-page
  (:require
   ["@dracula/dracula-ui" :as drac]))

(def contact-page
  [:div
   [:> drac/Paragraph
    "Verify who I am (if you trust GPG) using "
    [:> drac/Text
     {:color "orange"
      :as "a"
      :href "https://keybase.io/lucianolaratelli"}
     "Keybase"]]

   [:> drac/Paragraph
    "Shoot me an "
    [:> drac/Text
     {:color "red"
      :as "a"
      :href "mailto:luciano@laratel.li"}
     "email"]]

   [:> drac/Paragraph
    "Read some of my code at "
    [:> drac/Text
     {:color "green"
      :as "a"
      :href "https://github.com/lucianolaratelli"}
     "GitHub"]]

   [:> drac/Paragraph
    "Read my inane thoughts via "
    [:> drac/Text
     {:color "cyan"
      :as "a"
      :href "https://twitter.com/lucianolaratell"}
     "Twitter"]]])
