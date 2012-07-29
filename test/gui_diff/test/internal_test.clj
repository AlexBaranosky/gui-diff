(ns gui-diff.test.internal-test
  (:use gui-diff.internal
        clojure.test))


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

(deftest test-1
  (is (= [{:test-name "test-fail"
              :file-info "NO_SOURCE_FILE:6"
              :expected "1\n"
              :actual "2\n"}
             {:test-name "test-fail-more"
              :file-info "NO_SOURCE_FILE:67"
              :expected "{:A 1}\n"
              :actual "{:a 1, :b 2, :c 3, :d 4, :e 5}\n"}]
            (ct-report-str->failure-maps multiple-FAILs))))

(deftest test-2
  (is (= [{:test-name "test-fail"
              :file-info "NO_SOURCE_FILE:6"
              :expected "1\n"
              :actual "2\n"}]
            (ct-report-str->failure-maps single-FAIL))))

(deftest test-33
  (is (= [{:test-name "test-fail-high"
              :file-info "NO_SOURCE_FILE:67"
              :expected "{:A 1}\n\n \n \n \n "
              :actual "{:a 11111,\n :b 2,\n :c 33333777776666622222921347128472847124871472340N,\n :d 4,\n :e 55555}\n"}]
            (ct-report-str->failure-maps different-heights-FAIL))))

;; TODO: test exps/acts/ with differenet lengths/heights space properly in the
;; diff report



