(ns gui-diff.core
  (:require [clojure.pprint :as pp]
            [clojure.java.shell :as sh])
  (:import java.io.File))

(defn- map-values [f m]
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

(defn- diff-prog [filename-1 filename-2]
  (let [os (System/getProperty "os.name")]
    (case os
      "Mac OS X" ["opendiff" filename-1 filename-2]
      "Linux" (if (= 0 (:exit (sh/sh "which" "meld")))
                ["meld" filename-1 filename-2]
                ["xterm" "-e"
                 (apply str "diff " filename-1 " " filename-2 ";read -p 'press Enter to continue'")])
      (throw (Exception. (str "gui-diff does not support your OS: " os))))))

(defn- diff-tool
  [filename-1 filename-2]
  (let [^String    env-var "DIFFTOOL"
        ^java.util.Map env (System/getenv)]
    (if (.containsKey env env-var)
      [(.get env env-var) filename-1 filename-2]
      (diff-prog filename-1 filename-2))))

(defn gui-diff
  "Display a visual diff of two data structures, a and b. On Mac uses FileMerge.
   On Linux, first tries to use Meld, then falls back to diff."
  [a b]
  (let [a-pp (with-out-str (pp/pprint (nested-sort a)))
        b-pp (with-out-str (pp/pprint (nested-sort b)))
        file-1 (File/createTempFile "a_gui_diff" ".txt")
        file-2 (File/createTempFile "b_gui_diff" ".txt")
        filename-1 (.getCanonicalPath file-1)
        filename-2 (.getCanonicalPath file-2)]
    (spit file-1 a-pp)
    (spit file-2 b-pp)
    (.start (Thread. (fn [] (apply sh/sh (diff-tool filename-1 filename-2)))))))
