(ns li.laratel.programs.programs
  (:require
   [li.laratel.util :as util]))

(defn programs [_request]
  (util/site-page
   {:title "Luciano Laratelli's Programs"
    :description "Listing of some smaller programs written by Luciano Laratelli"}
   [:table.listing-table
    [:tr.lsiting-row
     [:td.listing-row.pad-right "2024-03-05"]
     [:td.listing-row.pad-left [:a {:href (str "/programs/" "named-tab")} "NamedTab"] " is a silly-but-useful tool that lets you set the title of a page."]]]

;; [:p
   ;;  [:table {:style
   ;;           {:border-collapse "collapse"}}
   ;;   [:tr
   ;;    (util/table-row
   ;;     [:a {:href (str "/programs/" "reconciler")} "Reconciler"])]]]
   ))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn named-tab []
  (util/site-page
   {:title "NamedTab"
    :description "NamedTab is a silly-but-useful tool that lets you set the title of a page."}
   [:p
    "This page lets you set the name of the current tab. It uses "
    [:a {:href "https://shields.io/"} "Shields.io"]
    " to generate a favicon and will maintain a consistent color for the same
    string. I use it in conjuction with "
    [:a {:href "https://addons.mozilla.org/en-US/firefox/addon/tree-style-tab/"} "Tree Style Tab"]
    " and some "  [:a {:href "https://git.sr.ht/~luciano/.home/tree/main/item/userChrome.css"} "CSS"]
    " my friend Elijah dug up (thanks again!) to achieve this functionality: "
    [:p [:img.center.max-width-gif-316 {:src "/img/named-tab.gif"
                                        :loading "lazy"}]]
    [:div
     "Your desired tab name:"
     [:input {:id "the-input"}]
     [:script
      "const updateTitle = () => {\n            const title = document.getElementById('the-input').value;\n            document.title = title;\n        }\n\n        // https://stackoverflow.com/a/260876\n        const changeFavicon = (src) => {\n            var link = document.querySelector(\"link[rel~='icon']\");\n            if (!link) {\n                link = document.createElement('link');\n                link.rel = 'icon';\n                document.head.appendChild(link);\n            }\n            link.href = src;\n        }\n\n        // https://stackoverflow.com/a/16348977\n        const stringToColor = (str) => {\n            let hash = 0;\n            str.split('').forEach(char => {\n                hash = char.charCodeAt(0) + ((hash << 5) - hash)\n            })\n            let color = ''\n            for (let i = 0; i < 3; i++) {\n                const value = (hash >> (i * 8)) & 0xff\n                color += value.toString(16).padStart(2, '0')\n            }\n            return color\n        }\n\n        const updateFavicon = () => {\n            const title = document.getElementById('the-input').value;\n            const color = stringToColor(title);\n            const faviconUrl = `https://img.shields.io/badge/${title[0].toUpperCase()}-${color}`\n\n            changeFavicon(faviconUrl);\n        }\n\n        document.getElementById('the-input').addEventListener(\"change\", updateTitle);\n        document.getElementById('the-input').addEventListener(\"change\", updateFavicon);"]]]))

(defn program [{{:keys [program-id]} :path-params}]
  ;;sorry
  (let [program-fn (ns-resolve (find-ns 'li.laratel.programs.programs) (symbol program-id))]
    (program-fn)))
