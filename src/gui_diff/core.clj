(ns gui-diff.core
  (:require [clojure.pprint :as pp]
            [clojure.java.shell :as sh]
            [clojure.test :as ct]
            [clojure.string :as str])
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

(def p ^{:doc "Nested sorts, then pretty prints a clojure data structure."}
  (comp pp/pprint nested-sort))

(defn p-str
  "Like p but prints the output to a string."
  [x]
  (with-out-str (p x)))

(defn gui-diff
  "Display a visual diff of two data structures, a and b. On Mac uses FileMerge.
   On Linux, first tries to use Meld, then falls back to diff."
  [a b]
  (let [file-1 (File/createTempFile "a_gui_diff" ".txt")
        file-2 (File/createTempFile "b_gui_diff" ".txt")]
    (spit file-1 (p-str a))
    (spit file-2 (p-str b))
    (diff-files file-1 file-2)))

(defn num-lines [s]
  (count (str/split s #"\n")))

(defn pad-with-extra-lines [s n]
  (if (zero? n)
    s
    (str/join "\n " (cons s (repeat n "")))))

(defn failure-maps->gui-diff-report-left-and-right-side [failure-maps]
  (let [expecteds (str/join "\n"
                            (mapcat (fn [{:keys [test-name file-info expected actual]}]
                                      ["============================================" (format "\"%s\" :: (%s)" test-name file-info) "============================================" expected ])
                                    failure-maps))
        actuals (str/join "\n"
                          (mapcat (fn [{:keys [test-name file-info expected actual]}]
                                    ["============================================" (format "\"%s\" :: (%s)" test-name file-info) "============================================" actual ])
                                  failure-maps))]
    [expecteds actuals]))

(def ^{;:private true
       :doc "Capture groups: 1. name of test, 2. filename and line, 3. failing s-expr"}
  clojure-test-failure-regex
  #"(?ms).*?FAIL in \(([^)]+)\) \(([^)]+)\)\nexpected: \(= [^\n]+\)\n  actual: \(not ([^\n]+)\)")

(defn normalize-line-count [str1 str2]
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

(defn ct-output->gui-diff-report [ct-report-str]
  (-> ct-report-str
      ct-report-str->failure-maps
      failure-maps->gui-diff-report-left-and-right-side))

(defn upgrade-to-gui-diff
  ""
  [test-fn]
  (fn [& args]
    (let [sw (java.io.StringWriter.)]
      (binding [ct/*test-out* sw]
        (apply test-fn args))

      (let [[expecteds actuals]  (ct-output->gui-diff-report (str sw))]
        (when-not (empty? expecteds)
          (let [file-1 (File/createTempFile "a_gui_diff_report" ".txt")
                file-2 (File/createTempFile "b_gui_diff_report" ".txt")]
            (spit file-1 expecteds)
            (spit file-2 actuals)
            (diff-files file-1 file-2))))
      
      (print (str sw)))))

(defmacro with-gui-diff
  ""
  [& body]
  `((upgrade-to-gui-diff (fn [] ~@body))))

(def run-tests++ ^{:doc "Wraps clojure.test/run-tests to generate a gui-diff report"}
  (upgrade-to-gui-diff ct/run-tests))

(def run-all-tests++ ^{:doc "Wraps clojure.test.run-all-tests to generate a gui-diff report"}
  (upgrade-to-gui-diff ct/run-all-tests))


;; TODO July 27, 2012, Alex -- create internal ns for private fns ??

;;;; Tests

(def single-FAIL "FAIL in (test-fail) (NO_SOURCE_FILE:6)
expected: (= 1 2)
  actual: (not (= 1 2))")

(def multiple-FAILs "FAIL in (test-fail) (NO_SOURCE_FILE:6)
expected: (= 1 2)
  actual: (not (= 1 2))

FAIL in (test-fail-more) (NO_SOURCE_FILE:67)
expected: (= {:A 1} {:a 1, :b 2, :c 3, :d 4, :e 5})
  actual: (not (= {:A 1} {:a 1, :c 3, :b 2, :d 4, :e 5}))")

(def different-heights-FAIL "FAIL in (test-fail-high) (NO_SOURCE_FILE:67)
expected: (= {:A 1} {:a 1, :b 2, :c 3, :d 4, :e 5})
  actual: (not (= {:A 1} {:a 11111, :c 33333777776666622222921347128472847124871472340, :b 2, :d 4, :e 55555}))")

(ct/deftest test-1
  (ct/is (= [{:test-name "test-fail"
              :file-info "NO_SOURCE_FILE:6"
              :expected "1\n"
              :actual "2\n"}
             {:test-name "test-fail-more"
              :file-info "NO_SOURCE_FILE:67"
              :expected "{:A 1}\n"
              :actual "{:a 1, :b 2, :c 3, :d 4, :e 5}\n"}]
            (ct-report-str->failure-maps multiple-FAILs))))

(ct/deftest test-2
  (ct/is (= [{:test-name "test-fail"
              :file-info "NO_SOURCE_FILE:6"
              :expected "1\n"
              :actual "2\n"}]
            (ct-report-str->failure-maps single-FAIL))))

(ct/deftest test-33
  (ct/is (= [{:test-name "test-fail-high"
              :file-info "NO_SOURCE_FILE:67"
              :expected "{:A 1}\n\n \n \n \n "
              :actual "{:a 11111,\n :b 2,\n :c 33333777776666622222921347128472847124871472340N,\n :d 4,\n :e 55555}\n"}]
            (ct-report-str->failure-maps different-heights-FAIL))))

;; TODO: test exps/acts/ with differenet lengths/heights space properly in the
;; diff report
