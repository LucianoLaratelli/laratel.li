(ns laratelli.components.tabs
  (:require ["@dracula/dracula-ui" :as drac]
            [laratelli.routes :as routes]
            [reitit.frontend.easy :as rfe]))

(defn one-tab
  [tab active]
  (let [class (if (= active :active) "drac-tab drac-tab-active" "drac-tab")]
    (vector :li {:class class}
            [:> drac/Anchor {:class "drac-tab-link drac-text"
                             :href (rfe/href (get routes/refs (keyword tab)))}
             tab])))

(defn make-tabs [active-tab]
  (let [tabs ["home"
              ;; "about"
              "contact" "cv" "posts"]
        tab-colors {"home" "purple"
                    "about" "red"
                    "contact" "yellow"
                    "cv" "green"
                    "posts" "cyan"}]
    (into [:> drac/Tabs
           {:color (get tab-colors active-tab)}]
          (map #(one-tab % (if (= % active-tab)
                             :active
                             :inactive))
               tabs))))
