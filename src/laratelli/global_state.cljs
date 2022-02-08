(ns laratelli.global-state
  "An unfortunate bit of global state. Sorry Rich"
  (:require [reagent.core :as r]))

;parsed markdown and post metadata
(defonce post-data (r/atom nil))

;list of {:prev :next} for each blog post
(defonce post-links (r/atom nil))

;This is the state of the whole site.
(defonce match (r/atom nil))

;dispatch_once equivalent
(defonce didPerformInitialSetup (r/atom false))
