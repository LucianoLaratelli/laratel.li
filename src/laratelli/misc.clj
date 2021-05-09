(ns laratelli.misc
  (:require
   [clojure.java.io :as io]
   [clojure.string :as s]
   [cybermonday.core :as cm]))

(defn get-file [fname]
  (slurp (io/file fname)))

(defmacro get-resource [fname]
  (slurp (io/resource fname)))

(def blog-files
  (->> "/Users/luciano/Dropbox/projects/laratel.li/resources/blog"
       (io/file)
       (file-seq)
       (map str)
       (rest)
       (into [])
       ; macOS... truly despicable
       (filter #(not (= (last (s/split % #"/")) ".DS_Store")))))

(defn make-post-id
  "generate a stable, web-safe identifier for a post title. to be used as part of a post's URL"
  [title]

  (as-> title t
    (s/split t #"\(.*\)|\[.*\]")
    (map s/trim t)
    (s/join " " t)
    (s/replace t #"\s+|/" "-")
    (s/replace t #"'|," "")))

(defn make-id-post-pair [post-file]
  (let [f (get-file post-file)
        md (cm/parse-front f)
        title (:title md)
        date (:date md)
        id (make-post-id title)]
    {:date date
     :title title
     :id id
     :md f}))

(defmacro blog-posts [_]
  (into [] (map make-id-post-pair blog-files)))
