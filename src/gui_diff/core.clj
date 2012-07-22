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

(defn- diff-prog 
  "Returns a vector of diff program name with params that is used to show the diff
   Ubuntu - try to use meld, fallback on diff
   Mac - opendiff"
  [fn1 fn2]
  (if (= "Linux" (System/getProperty "os.name"))
    (if (= 0 (:exit(sh/sh "which" "meld")))
      ["meld" fn1 fn2]
      ["xterm" "-e"
       (apply str "diff " fn1 " " fn2 ";read -p 'press Enter to continue'")])
    ["opendiff" fn1 fn2]))
             
(defn gui-diff
  "Display a visual diff of two data structures, using Mac's FileMerge tool."
  [a b]
  (let [a-pp (with-out-str (pp/pprint (nested-sort a)))
        b-pp (with-out-str (pp/pprint (nested-sort b)))]
    (spit (File. "/tmp/a.txt") a-pp)
    (spit (File. "/tmp/b.txt") b-pp)
    (.start (Thread. (fn [] (apply sh/sh (diff-prog "/tmp/a.txt" "/tmp/b.txt")))))))
