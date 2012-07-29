(ns gui-diff.test.core-test
  (:use gui-diff.core
        clojure.test))


(deftest test-all-public-var-have-doc-strings
  (is (empty? (->> (ns-publics 'gui-diff.core)
                   vals
                   (remove (comp :doc meta))
                   (map str)))))