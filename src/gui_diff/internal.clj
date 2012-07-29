(ns gui-diff.internal
  (:require [clojure.java.shell :as sh]
            [clojure.pprint :as pp]
            [clojure.string :as str])
  (:import java.io.File))

(defn- map-values [f m]
  (zipmap
    (keys m)
    (map f (vals m))))

(defn nested-sort [x]
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


(def p ^{:doc "Nested sorts, then pretty prints a clojure data structure."}
  (comp pp/pprint nested-sort))

(defn p-str
  "Like p but prints the output to a string."
  [x]
  (with-out-str (p x)))

(defn- diff-prog [filename-1 filename-2]
  (let [os (System/getProperty "os.name")]
    (case os
      "Mac OS X" ["opendiff" filename-1 filename-2]
      "Linux" (if (= 0 (:exit (sh/sh "which" "meld")))
                ["meld" filename-1 filename-2]
                ["xterm" "-e"
                 (apply str "diff " filename-1 " " filename-2 ";read -p 'press Enter to continue'")])
      (throw (Exception. (str "gui-diff does not support your OS: " os))))))

(defn- diff-tool [filename-1 filename-2]
  (let [^String    env-var "DIFFTOOL"
        ^java.util.Map env (System/getenv)]
    (if (.containsKey env env-var)
      [(.get env env-var) filename-1 filename-2]
      (diff-prog filename-1 filename-2))))

(defn diff-files [^File file-1 ^File file-2]
  (let [filename-1 (.getCanonicalPath file-1)
        filename-2 (.getCanonicalPath file-2)]
   (.start (Thread. (fn [] (apply sh/sh (diff-tool filename-1 filename-2)))))))

(defn- num-lines [s]
  (count (str/split s #"\n")))

(defn- pad-with-extra-lines [s n]
  (if (zero? n)
    s
    (str/join "\n " (cons s (repeat n "")))))

(defn- format-failure-maps [failure-maps actual-or-expected]
  (str/join "\n"
            (mapcat (fn [{:keys [test-name file-info expected actual] :as failure-map}]
                      ["============================================"
                       (format "\"%s\" :: (%s)" test-name file-info)
                       "============================================"
                       (actual-or-expected failure-map)])
                    failure-maps)))

(defn failure-maps->gui-diff-report-left-and-right-side [failure-maps]
  [(format-failure-maps failure-maps :expected) (format-failure-maps failure-maps :actual)])

(def ^{:private true
       :doc "Capture groups: 1. name of test, 2. filename and line, 3. failing s-expr"}
  clojure-test-failure-regex
  #"(?ms).*?FAIL in \(([^)]+)\) \(([^)]+)\)\nexpected: \(= [^\n]+\)\n  actual: \(not ([^\n]+)\)")

(defn- normalize-line-count [str1 str2]
  (let [str1 (p-str str1)
        str2 (p-str str2)
        str1-lines (num-lines str1)
        str2-lines (num-lines str2)
        [str1 str2] (if (> str1-lines str2-lines)
                      [str1 (pad-with-extra-lines str2 (- str1-lines str2-lines))]
                      [(pad-with-extra-lines str1 (- str2-lines str1-lines)) str2])]
    [str1 str2]))

(defn ct-report-str->failure-maps [ct-report-str]
  (for [[_ test-name file-info actual-line] (re-seq clojure-test-failure-regex ct-report-str)
        :let [[_fn_ expected actual] (read-string actual-line)
              [formatted-exp formatted-act] (normalize-line-count expected actual)]]
    {:test-name test-name
     :file-info file-info
     :expected formatted-exp
     :actual formatted-act}))