(ns gui.test.parser-test
  (:use clojure.test
        gui.internal.parser
        gui.diff))


(deftest test-parser
  (is (= ['= '(bob) '(mary)] (parse-= "(= (bob) (mary))")))

  (is (= ['= #{'bob} #{'mary}] (parse-= "(= #{bob} #{mary})")))
  (is (= ['= ['bob] ['mary]] (parse-= "(= [bob] [mary])")))
  (is (= ['= 'bob 'mary] (parse-= "(= bob mary)")))

  (is (= ['= {:a 1} {:b 2}] (parse-= "(= {:a 1} {:b 2})")))

  (is (= ['= 'bob #{'mary}] (parse-= "(= bob #{mary})")))

  ;; if something can't be read, then use it's string representation instead
  (is (= ["{:a}" 'bob #{'mary}] (parse-= "({:a} bob #{mary})"))))

