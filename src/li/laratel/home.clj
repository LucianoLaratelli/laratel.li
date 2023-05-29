(ns li.laratel.home
  (:require [li.laratel.util :refer [pars site-page]]))

(defn home [_]
  (site-page
   {:title "Luciano Laratelli"
    :description "Home page of Luciano Laratelli's personal website. Contains a
    short bio and various contacts methods, including
    email (luciano@laratel.li), links to GitHub and SourceHut profiles,
    LinkedIn, PGP key, and my parens.social mastodon instance username."
    :has-code? false}
   (pars
    "Hello and welcome!"
    "This is my personal website."
    "I'm a software developer and engineer based out of Miami Beach. My
    technical interests include functional programming, especially in Clojure,
    as well as herding Linux servers. I'm an Emacs enthusiast. Before I got into
    computers, I spent far too much time thinking about chemistry."
    ["You can reach me by email at " [:a {:href "mailto:luciano@laratel.li"} "luciano@laratel.li"] "."]
    ["I publish open-source projects on both "
     [:a {:href "https://git.sr.ht/~luciano/"} "SourceHut"]
     " and "
     [:a {:href "https://github.com/LucianoLaratelli/"} "GitHub"] "."]

    ["I'm unfortunately on " [:a {:href "https://www.linkedin.com/in/luciano-laratelli-663851a1/"} "LinkedIn"] "."]

    ["Interested in PGP? I'm on " [:a {:href "https://keybase.io/lucianolaratelli"} "Keybase"] ". "
     "You can also " [:a {:href "/public-key.asc"} "view"] " my public key."]
    [[:a
      {:href "https://parens.social/@luciano"
       :rel "me"}
      "Let's federate!"]]

    ["This site is written in Clojure; its source code lives "
     [:a {:href "https://github.com/LucianoLaratelli/laratel.li"} "on my GitHub"]
     ". I used " [:a
                  {:href "https://neat.joeldare.com/"} "neat.css"]
     ", with some modifications. Blog post parsing made possible thanks to the
     wonderful "
     [:a {:href "https://github.com/kiranshila/cybermonday"} "cybermonday"] " library."])))
