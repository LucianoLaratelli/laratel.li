(ns laratelli.pages
  "laratel.li pages. anything that refers to a route needs to be in this file."
  (:require
   ["@dracula/dracula-ui" :as drac]
   [reitit.frontend.easy :as rfe]
   [laratelli.posts :refer [parse]]
   [laratelli.contact-page :refer [contact-page]])
  (:require-macros [laratelli.misc :refer [blog-posts get-resource]]))

(def refs
  {:about ::about
   :home ::home
   :contact ::contact
   :cv ::cv
   :posts ::posts})

(defn home []
  [:div
   [:> drac/Paragraph
    "Hi, welcome to my site."]
   [:> drac/Heading
    {:size "L"
     :color "white"}
    "Recent blog posts:"]])

(defn about []
  [:div
   [:> drac/Paragraph
    "I'm an all-around regular guy"]])

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

(def education-page
  [:div
   [:> drac/Heading {:size "xl"}
    "Education"]
   [:div
    [:> drac/Heading {:size "l"}
     "Master of Science in Computer Science"]
    (employment-badges ["University of South Florida"
                        "May 2021"
                        "Tampa, FL"])]
   [:div
    [:> drac/Heading {:size "l"}
     "Bachelor of Science in Chemistry"]
    (employment-badges ["University of South Florida"
                        "May 2018"
                        "Tampa, FL"])]
   [:div
    [:> drac/Heading {:size "l"}
     "High School"]
    (employment-badges ["Belen Jesuit Preparatory School"
                        "May 2013"
                        "Miami, FL"])]
   [:div
    [:> drac/Heading {:size "l"}
     "Other"]
    [:> drac/Text "I completed eighteen credit hours in the Ph.D. Chemistry
    program at USF between August 2018 and May 2019, focusing on research in
    computational chemistry. I decided to transfer into the Computer Science
    program sometime during the spring of 2019, and began taking classes for
    that degree that summer."]]])

(def other-work-page
  [:div
   [:> drac/Heading {:size "xl"}
    "Other Work Experience"]
   [:div
    [:> drac/Heading {:size "l"}
     "Apple (retail)"]
    (employment-badges ["Technical Specialist => Technical Expert"
                        "July 2019 -> November 2020"
                        "Tampa, FL"])
    [:> drac/Text
     "I worked at the Genius Bar, doing assessment and resolution
    of software issues that occurred with Apple's mobile offerings: iPhone,
    iPad, and Apple Watch. In February of 2020 I was promoted to Technical
    Expert, which added physical repairs on iPhones to my responsibilities. I
    was consistently in the top five employees for troubleshooting sessions
    completed per hour and lowest average session duration. I also assisted the
    most customers out of anyone on the team in the fourth quarter of 2019."]]
   [:div
    [:> drac/Heading {:size "l"}
     "University of South Florida Department of Chemistry"]
    (employment-badges ["Graduate Teaching Assistant"
                        "August 2018 -> May 2019"
                        "Tampa, FL"])
    [:> drac/Text
     "I worked as a Lab TA for the General Chemistry I lab, where I was responsible
for teaching students semester basic and intermediate experimental chemistry
techniques, as well as the chemical theory behind the experiments performed.
Taught students the basics of data analysis and reporting.Responsible for
teaching students proper laboratory safety techniques and ensuring their safety
at all times in the lab. This position required the communication of ideas using
distinct methods, handling multiple tasks at once, and managing groups of people
to reach a common goal."]]])

(defn cv []
  [:div
   [:> drac/Heading {:size "xl"}
    "Professional Work Experience"]
   [:> drac/Heading {:size "m"}
    "Flexibits, Inc."]
   [:> drac/List {:style {:padding-left 0
                          :margin -1}}

    [:li {:style {:margin-top ".5em"
                  :margin-bottom ".5em"}}
     [:> drac/Badge {:variant "outline" :color "animated"
                     :m "xs" :style {:margin-left "0em"
                                     :padding-left 6
                                     :padding-right 6
                                     :padding-top 2
                                     :padding-bottom 2}}
      "Software Engineer"]]
    [:li
     {:style {:margin-top ".5em"
              :margin-bottom ".5em"}}
     [:> drac/Badge {:variant "subtle" :color "orange"
                     :m "xs" :style {:margin-left "0em"
                                     :padding-left 6
                                     :padding-right 6
                                     :padding-top 2
                                     :padding-bottom 2}}
      "March 2021 -> Present"]]
    [:li
     {:style {:margin-top ".5em"
              :margin-bottom ".5em"}}
     [:> drac/Badge {:variant "normal" :color "purpleCyan"
                     :m "xs" :style {:margin-left "0em"
                                     :padding-left 6
                                     :padding-right 6
                                     :padding-top 2
                                     :padding-bottom 2}}
      "Remote"]]]
   [:> drac/Text
    " I work on Fantastical and Cardhop for both mac and iOS. I'm the main
developer working on Fantastical for the Apple Watch, and maintain our Shortcuts
since migrating them to macOS. Lots of Objective-C with a little bit of Swift
and SwiftUI sprinkled in."]
   [:> drac/Divider {:color "orange"}]
   (parse (get-resource "cv/skills.md"))
   [:> drac/Divider {:color "orange"}]
   (parse (get-resource "cv/projects.md"))
   [:> drac/Divider {:color "orange"}]
   education-page
   [:> drac/Divider {:color "orange"}]
   other-work-page
   [:> drac/Divider {:color "orange"}]
   (parse (get-resource "cv/awards.md"))])

(defn contact []
  contact-page)

(defn make-id-content-pair [post-data]
  (let [id (keyword (:id post-data))
        data (dissoc post-data :id)]
    {id data}))

;;(into (sorted-map-by (fn [key1 key2] (compare (key2 post-thing) (key1 post-thing)))) post-thing)

(def post-data
  (into {} (reverse
            (map make-id-content-pair (blog-posts nil)))))

(defn get-list-item-anchor [item]
  (let [id (key item)
        meta (val item)
        title (:title meta)
        date (:date meta)]
    [:li
     {:class ["drac-text" "drac-text-white"]
      :key id
      :value (str date)}
     [:> drac/Anchor {:href (rfe/href ::post {:id id})} title]]))

(defn posts []
  [:ol
   (map get-list-item-anchor post-data)])

(defn blog-post [match]
  (let [id (get-in match [:parameters :path :id])
        post-info ((keyword id) post-data)
        title (:title post-info)
        date (str (:date post-info))
        md (:md post-info)]
    [:div [:> drac/Heading {:size "xl"} title]
     [:> drac/Heading {:size "md"} date]
     [:> drac/Button
      {:size "xs"
       :variant "ghost"
       :as "a"
       :href (rfe/href ::posts)}
      "<- to posts"]
     [:> drac/Divider {:color "purple"}]
     (parse md)
     [:> drac/Divider {:color "purple"}]
     [:> drac/Button
      {:size "xs"
       :variant "ghost"
       :as "a"
       :href (rfe/href ::posts)}
      "<- to posts"]]))

(def routes
  [["/"
    {:name ::home
     :view home}]
   ["/about"
    {:name ::about
     :view about}]
   ["/posts"
    [""
     {:name ::posts
      :view posts}]
    ["/:id"
     {:name ::post
      :view blog-post
      :parameters {:path {:id string?}}}]]
   ["/cv"
    {:name ::cv
     :view cv}]
   ["/contact"
    {:name ::contact
     :view contact}]])

(defn get-id-date-pair [m]
  {(key m)
   (:date (get post-data (key m)))})
