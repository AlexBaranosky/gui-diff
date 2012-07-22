(ns gui-diff.core
  (:require [clojure.pprint :as pp]
            [clojure.java.shell :as sh])
  (:import java.io.File))

(defn- map-values
  "Apply a function on all values of a map and return the corresponding map (all keys untouched)"
  [f m]
  (zipmap
    (keys m)
    (map f (vals m))))

(defn- nested-sort [x]
  (cond (sequential? x)
    (if (instance? java.lang.Comparable (first x))
      (sort (map nested-sort x))
      (map nested-sort x))

    (map? x)
    (if (and (not= {} x)
             (instance? java.lang.Comparable (key (first x))))
      (into (sorted-map) (map-values nested-sort x))
      (map-values nested-sort x))

    :else
    x))

(defn gui-diff
  "Display a visual diff of two data structures, using Mac's FileMerge tool."
  [a b]
  (let [file-a (File. "/tmp/a.txt")
        file-b (File. "/tmp/b.txt")
        a-pp (with-out-str (pp/pprint (nested-sort a)))
        b-pp (with-out-str (pp/pprint (nested-sort b)))]
    (spit file-a a-pp)
    (spit file-b b-pp)

    (sh/sh "opendiff" "/tmp/a.txt" "/tmp/b.txt")))