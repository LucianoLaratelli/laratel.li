(ns build
  (:require [clojure.tools.build.api :as b]))

(def class-dir "classes")

(def basis (b/create-basis {:project "deps.edn"}))

(def jar-file "luciano-service.jar")

(defn clean [_]
  (b/delete {:path "target"}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn uberjar [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src" "resources"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file jar-file
           :basis basis
           :main 'li.laratel.core}))
