(ns li.laratel.lowering
  (:require
   [clojure.string :as str]
   [clygments.core :as clygments]
   [cybermonday.core :as cm]))

(def footnote-count-for-post (atom 1))

(defn post-id
  "generate a stable, web-safe identifier for a post title. to be used as part of a post's URL"
  [title]
  (if (nil? title)
    (str  (java.util.UUID/randomUUID))
    (as-> title t
      (str/split t #"\(.*\)|\[.*\]|\+")
      (map str/trim t)
      (str/join " " t)
      (str/replace t #"\s+|/" "-")
      (str/replace t #"'|," ""))))

(defn lower-heading [[_ attrs & [body]]]
  (let [level (:level attrs)
        header-id (post-id body)
        header-ref (str "#" header-id)]
    [(keyword (str "h" level))
     [:a.aal_anchor
      {:href header-ref,
       :aria-hidden "true",
       :id header-id}
      [:svg
       {:aria-hidden "true",
        :class "aal_svg",
        :height "16",
        :version "1.1",
        :viewBox "0 0 16 16",
        :width "16"}
       [:path
        {:fill-rule "evenodd",
         :d
         "M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"}]]]
     body]))

(defn lower-footnote [[_ {:keys [id]}]]
  (let [ret [:sup {:id (str "fnref-" id)}
             [:a {:href (str "#fn-" id)} @footnote-count-for-post]]]
    (swap! footnote-count-for-post inc)
    ret))

(defn lower-fenced-code-block [[_ {:keys [language]
                                   :as guh} code]]
  (let [language (cond (str/blank? language) "text"
                       (= "emacs-lisp" language) "lisp"
                       (= "elisp" language) "lisp"
                       :else language)]

    (clygments/highlight code (keyword language) :html)))

(defn lower-link-ref [[_ {:keys [href title]} body]]
  (when href
    (if (str/starts-with? href "/img/") ;; not a hack. not brittle. always guaranteed to work.
      [:a {:href href}
       [:img {:src href
              :alt body}]]
      [:a {:href href
           :title title}
       body])))

(defn guh [[type args body]]
  (vector type
          (merge args {:style {:white-space "pre-wrap"}})
          (str/escape body
                      {\< "&lt;",
                       \> "&gt;",
                       \& "&amp;"})))

(def lower-fns
  {:markdown/link-ref lower-link-ref
   :a lower-link-ref
   :markdown/heading lower-heading
   :code guh
   :markdown/fenced-code-block lower-fenced-code-block
   :markdown/footnote lower-footnote})

(defonce formatter
  (java.text.SimpleDateFormat. "yyyy-MM-dd"))

(defn format-simple-date [date]
  (.format formatter date))

(defn top-level-li? [v]
  (and
   (vector? v)
   (= (first v) :li)))

(defn parse [md]
  (let [{{:keys [date title]} :frontmatter
         :as parsed} (cm/parse-md md {:lower-fns lower-fns})
        date-str (format-simple-date date)
        ;; hack to avoid making a cybermonday PR
        top-level-lis (filter top-level-li? (-> parsed :body))
        parsed (if (seq top-level-lis)
                 (assoc parsed :body
                        (conj (vec (remove top-level-li? (-> parsed :body)))
                              [:hr]
                              (apply vector :ol.footnote {:start 0} top-level-lis)))
                 parsed)]
    (-> parsed
        (assoc
         :date-str date-str
         :date-int (-> date-str
                       (str/replace  #"-" "")
                       Integer/parseInt)
         :blog-post-id (post-id title))
        (dissoc :frontmatter)
        (merge (:frontmatter parsed)))))
