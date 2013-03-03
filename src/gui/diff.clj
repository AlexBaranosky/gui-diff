(ns gui.diff
  (:require [clojure.java.shell :as sh]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.test :as ct]
            [gui.internal.parser :as parser]
            [ordered.map :as om]
            [ordered.set :as os])
  (:import java.io.File))


(defn- map-keys [f m]
  (zipmap
   (map f (keys m))
   (vals m)))

(defn- last-piece-of-ns-qualified-class-name [clazz]
  (last (clojure.string/split (str clazz) #"\.")))

(defn- grouped-comparables-and-uncomparables [xs]
  (let [[comparable uncomparable] ((juxt filter remove) #(instance? java.lang.Comparable %) xs)
        group+format+sort (fn [xs]
                            (->> (group-by class xs)
                                 (map-keys last-piece-of-ns-qualified-class-name)
                                 (into (sorted-map))))]
    [(group+format+sort comparable)
     (group+format+sort uncomparable)]))

(defn nested-sort
  "Sorts two nested collections for easy visual comparison.
   Sets and maps are converted to order-sets and ordered-maps."
  [x]
  (letfn [(seq-in-order-by-class
            [class-name->items sort?]
            (for [[_clazz_ xs] class-name->items
                  x (if sort? (sort xs) xs)]
              x))
          (map-in-order-by-class
            [m class-name->keys sort?]
            (into (om/ordered-map)
                  (for [[_clazz_ ks] class-name->keys
                        k (if sort? (sort ks) ks)]
                    [k (nested-sort (get m k))])))]
    
    (cond (set? x)
          (let [[comps uncomps] (grouped-comparables-and-uncomparables x)]
            (into (os/ordered-set)
                  (concat (seq-in-order-by-class comps true)
                          (seq-in-order-by-class uncomps false))))
          
          (map? x)
          (let [[comps uncomps] (grouped-comparables-and-uncomparables (keys x))]
            (into (map-in-order-by-class x comps true)
                  (map-in-order-by-class x uncomps false)))

          (vector? x)
          (into [] (map nested-sort x))

          (list? x)
          (reverse (into '() (map nested-sort x)))
          
          :else
          x)))

(def ^{:doc "Nested sorts, then pretty prints a clojure data structure."}
  p
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

(defn- diff-files [^File file-1 ^File file-2]
  (let [filename-1 (.getCanonicalPath file-1)
        filename-2 (.getCanonicalPath file-2)]
   (.start (Thread. (fn [] (apply sh/sh (diff-tool filename-1 filename-2)))))))

(defn- num-lines [s]
  (count (str/split s #"\n")))

(defn- pad-with-extra-lines [s n]
  (apply str s (repeat n "\n ")))

(defn- format-failure-maps [failure-maps actual-or-expected]
  (str/join "\n"
            (mapcat (fn [{:keys [test-name file-info expected actual] :as failure-map}]
                      ["============================================"
                       (format "\"%s\" :: (%s)" test-name file-info)
                       "============================================"
                       (actual-or-expected failure-map)])
                    failure-maps)))

(defn- failure-maps->gui-diff-report-left-and-right-side [failure-maps]
  [(format-failure-maps failure-maps :expected) (format-failure-maps failure-maps :actual)])

;; TODO: maybe figure out how to get this work in one pass of a larger regex
;; like: `.*FAIL in \((.+)\) \((.+)\)\nexpected: \(\S+ .+\)\n  actual: \(not (.+)` 


(def ^{:private true
       :doc "Capture groups: 1. name of test, 2. filename and line"}
   ct-test-info-regex
  #".*FAIL in \((.+)\) \((.+)\)")

(def ^{:private true
       :doc "Capture groups: 1. failing s-expr"}
  clojure-test-failure-regex
  #"\nexpected: \(\S+ .+\)\n.*  actual: \(not (.+)\)")

(defn- make-line-count-same [str1 str2]
  (let [str1 (p-str str1)
        str2 (p-str str2)
        str1-lines (num-lines str1)
        str2-lines (num-lines str2)
        [str1 str2] (if (> str1-lines str2-lines)
                      [str1 (pad-with-extra-lines str2 (- str1-lines str2-lines))]
                      [(pad-with-extra-lines str1 (- str2-lines str1-lines)) str2])]
    [str1 str2]))

(defn- zip
  "[[:a 1] [:b 2] [:c 3]] ;=> [[:a :b :c] [1 2 3]]"
  [& seqs]
  (if (empty? seqs)
    []
    (apply map list seqs)))

(defn- reportable-failure? [[f _expected_ _actual_ :as failure]]
  (and (= 3 (count failure))
       (= f '=)))

(defn- ct-report-str->failure-maps [ct-report-str]
  (for [[[ _ test-name file-info] [_ actual-line]] (zip
                                                    (re-seq ct-test-info-regex ct-report-str)
                                                    (re-seq clojure-test-failure-regex ct-report-str))
        :let [[_fn_ expected actual :as failure] (parser/parse-= actual-line)
              [formatted-exp formatted-act] (make-line-count-same expected actual)]
        :when (reportable-failure? failure)]
    {:test-name test-name
     :file-info file-info
     :expected formatted-exp
     :actual formatted-act}))


(defn- gui-diff-strings [expected actual]
  (let [file-1 (doto (File/createTempFile (str "a_gui_diff" (java.util.UUID/randomUUID)) ".txt")
                 .deleteOnExit)
        file-2 (doto (File/createTempFile (str "b_gui_diff" (java.util.UUID/randomUUID)) ".txt")
                 .deleteOnExit)]
    (spit file-1 expected)
    (spit file-2 actual)
    (diff-files file-1 file-2)))

(defn gui-diff
  "Display a visual diff of two data structures, a and b. On Mac uses FileMerge.
   On Linux, first tries to use Meld, then falls back to diff."
  [a b]
  (gui-diff-strings (p-str a) (p-str b)))

(defn- ct-output->gui-diff-report [ct-report-str]
  (-> ct-report-str
      ct-report-str->failure-maps
      failure-maps->gui-diff-report-left-and-right-side))

(defn upgrade-to-gui-diff
  "Wrap any function that sends failure information to clojure.test's *test-out*
   and returns a new function that creates visual diff report of each failure
   that occurs when it is called."
  [test-fn]
  (fn [& args]
    (let [sw (java.io.StringWriter.)]
      (binding [ct/*test-out* sw]
        (apply test-fn args))

      (let [[expecteds actuals]  (ct-output->gui-diff-report (str sw))]
        (when-not (empty? expecteds)
          (gui-diff-strings expecteds actuals)))
      
      (print (str sw)))))

(defmacro with-gui-diff
  "Wrap any code that sends failure information to clojure.test's *test-out*
   to create visual diff report of each failure."
  [& body]
  `((upgrade-to-gui-diff (fn [] ~@body))))

(def ^{:doc "Wraps clojure.test/run-tests to generate a gui-diff report"}
  run-tests++
  (upgrade-to-gui-diff ct/run-tests))

(def ^{:doc "Wraps clojure.test.run-all-tests to generate a gui-diff report"}
  run-all-tests++
  (upgrade-to-gui-diff ct/run-all-tests))