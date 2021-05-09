(ns laratelli.parsed-posts
  "Macros that deal with getting blog posts off the filesystem."
  (:require
   [clojure.java.io :as io]
   [clojure.string :as s]
   [clojure.walk :as w]
   [cybermonday.core :as cm]
   [cybermonday.ir :as ir]
   [shadow.resource :as rc]))

;TODO: remove me after cv content is fully inlined
(defmacro get-resource [fname]
  (slurp (io/resource fname)))

(defn post-id
  "generate a stable, web-safe identifier for a post title. to be used as part of a post's URL"
  [title]
  (as-> title t
    (s/split t #"\(.*\)|\[.*\]|\+")
    (map s/trim t)
    (s/join " " t)
    (s/replace t #"\s+|/" "-")
    (s/replace t #"'|," "")))

(defn post-metadata
  "Get post metadata out of a markdown file. We do the parsing of markdown here in
  clj land (that is, at macro time) to save clients time when they load a post.
  We return a mapping from a post's id to the rest of its metadata. I've defined
  routes based on their ids, so when someone navigates to a page, we can get the
  id from the route and use this mapping to get the rest of the metadata (the
  content we display to the user. We'll stick the href in once the routes exist
  in cljs land."
  [post-file]
  (let [frontmatter (cm/parse-front post-file)
        ;; cm/parse-body takes lowering fns that we don't have yet. ir/md-to-ir
        ;; is the first step in that function, so we just tear it out here.
        body (let [[_ _ body] (re-matches cm/frontmatter-re post-file)]
               (ir/md-to-ir body))
        title (:title frontmatter)
        date (:date frontmatter)
        date-str (.format (new java.text.SimpleDateFormat
                               "yyyy-MM-dd") date)
        date-sort (as-> date-str s
                    (s/split s #"-")
                    (apply str s)
                    (Integer/parseInt s))
        id (post-id title)]
    (hash-map id
              {:datestr date-str
               :datesort date-sort
               :title title
               :md body
               :id id})))

(def blog-files
  ; bless thheller
  ; https://clojureverse.org/t/using-none-code-resources-in-cljs-builds/3745
  ; These have to be written out manually, but the benefit of being able to
  ; hot-reload the frontend when I change the markdown is worth the extra two
  ; seconds per blog post.
  (vector
   (rc/inline "blog/github_merging_PRs.md")
   (rc/inline "blog/lammps-system-dipole.md")
   (rc/inline "blog/lammps-total-velocities.md")
   (rc/inline "blog/llvm_classes.md")
   (rc/inline "blog/llvm_print.md")
   (rc/inline "blog/llvm_read.md")))

(defmacro parsed-posts []
  (->> blog-files
       (map post-metadata)
       (into {})
       (sort-by (comp :datesort second) >)
       (into {})
       w/keywordize-keys
       (list 'quote)))
