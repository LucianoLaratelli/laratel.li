(ns laratelli.posts
  "Lowering functions"
  (:require
   [reagent.core :as r]
   [cybermonday.core :as cm]
   [cybermonday.utils :as cmu]
   [cybermonday.ir :as ir]
   [cybermonday.lowering :as lowering]
   ["@dracula/dracula-ui" :as drac]
   ["react-syntax-highlighter/dist/esm/styles/prism/dracula" :default code-style]
   ["react-syntax-highlighter" :as highlighter]
   [clojure.string :as str]))

(enable-console-print!)

(def Highlighter (.-Prism highlighter))

(defn highlight-pre [props]
  [:pre {:class (:className props)}
   (:children props)])

(def heading-level ["xl" "lg" "md" "sm" "xs" "xs"])

(defn lower-heading [[_ attrs & [body]]]
  (let [level (dec (:level attrs))]
    [:> drac/Heading
     {:color "white"
      :size (nth heading-level level)}
     body]))

(defn lower-fenced-code-block [[_ {:keys [language]} code]]
  (let [language (if (str/blank? language) "text" language)]
    (js/console.log language)
    [:> Highlighter {:PreTag (r/reactify-component highlight-pre)
                     :class (str "language-" language)
                     :style code-style
                     :showLineNumbers true
                     :lineNumberStyle {:min-width "1.75rem"}
                     :language language
                     :codeTagProps {:class (str "language-" language)}}
     code]))

(defn lower-link-ref [[_ {:keys [reference]} body]]
  (js/console.log "buh")
  (js/console.log reference)
  (js/console.log body)
  (js/console.log "guh")
  (when reference
    [:> drac/Anchor {:color "cyan"
                     :hoverColor "yellowPink"
                     :href (:url (second reference))
                     :title (:title (second reference))}
     body]))

(defn lower-list-item [[_ _ & [body]]]
  (if (string? (body 2))
    [:li {:class "drac-text drac-text-white"} (body 2)]
    [:li {:class "drac-text drac-text-white"} body]))

(defn lower-wrap-component [component & [attrs]]
  (fn [[_ attr-map & body]]
    (apply vector :> component (merge attr-map attrs) body)))

(def lower-fns
  {:ul    (lower-wrap-component drac/List         {:color "purple"
                                                   :variant "unordered"})
   :ol     (lower-wrap-component drac/OrderedList {:color "purple"})
   :div    (lower-wrap-component drac/Box)
   :hr     (lower-wrap-component drac/Divider     {:color "orange"})
   ;; having this be a drac/Paragraph was causing me problems with lists like this:
   ;; - `code` and stuff
   ;; There would be a very ugly newline between the - and the rendered `code`.
   :p     (lower-wrap-component drac/Text)
   :em     (lower-wrap-component drac/Text        {:weight "semibold"})
   :strong (lower-wrap-component drac/Text        {:weight "bold"})
   :a      (lower-wrap-component drac/Anchor      {:color "cyanGreen"
                                                   :hoverColor "yellowPink"})
   :table  (lower-wrap-component drac/Table       {:color "cyan"
                                                   :style {:margin "auto"
                                                           :width "auto"}
                                                   :align "center"
                                                   :variant "striped"})
   :markdown/bullet-list-item lower-list-item
   :markdown/ordered-list-item lower-list-item
   :markdown/link-ref lower-link-ref
   :markdown/heading lower-heading
   :markdown/fenced-code-block lower-fenced-code-block})

(def default-attrs
  {:th {:class ["drac-text" "drac-text-white"]}
   :td {:class ["drac-text" "drac-text-white"]}
   :div {:class ["drac-text-white"]}
   :p {:class ["drac-text drac-line-height drac-text-white"]}})

(defn parse [md]
  (cm/parse-body md {:lower-fns lower-fns :default-attrs default-attrs}))
