(ns laratelli.components.tabs
  (:require ["@dracula/dracula-ui" :as drac]
            [laratelli.routes :as routes]
            [reitit.frontend.easy :as rfe]))

(defn one-tab
  [tab active]
  (let [class (if (= active :active)
                "drac-tab drac-tab-active"
                "drac-tab")]
    (vector :li {:class class}
            [:> drac/Anchor {:class "drac-tab-link drac-text"
                             :href (rfe/href (get routes/refs (keyword tab)))}
             tab])))

(defn make-tabs [active-tab]
  (let [tab-names ["home" "contact" "cv" "posts"]
        tab-colors ["purple" "yellow" "green" "cyan"]
        _ (assert (= (count tab-names) (count tab-colors)))
        tabs-with-color (apply assoc {} (interleave tab-names tab-colors))]
    (into [:> drac/Tabs
           {:color (get tabs-with-color active-tab)}]
          (map #(one-tab % (if (= % active-tab)
                             :active
                             :inactive))
               tab-names))))
