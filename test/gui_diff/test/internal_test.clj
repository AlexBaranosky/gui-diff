(ns gui-diff.test.internal-test
  (:use gui-diff.internal
        clojure.test))


(def single-FAIL "FAIL in (test-fail) (NO_SOURCE_FILE:6)
expected: (foo? 1 2)
  actual: (not (foo? 1 2))")

(def multiple-FAILs "

FAIL in (test-fail) (NO_SOURCE_FILE:6)
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
           :actual "{:a 11111,\n :b 2,\n :c 33333777776666622222921347128472847124871472340,\n :d 4,\n :e 55555}\n"}]
         (ct-report-str->failure-maps different-heights-FAIL))))

(def regression-string
  "
:starts-on \"1970-01-01 00:20:31\",
:percentage 50.0,
:offer true}


Testing furtive.serenity.migration-spec

FAIL in (test-migrate) (migration_spec.clj:859)
session data events
expected: (= session-data-event (migrate legacy-session-data-event))
  actual: (not (= {:timestamp 123450000, :data {:paid-ad true, :timezone \"EST\", :sushi true, :offer-model :random, :baseline-info {:enabled true, :starts-on 1231231, :percentage 50.0, :offer true}, :url-referrer \"http://www.twitter.com\", :bucket 123, :paid-search-terms [\":apple\" \":banana\" \":cherry\"], :session-start-time 12412444, :landing-url \"http://www.shoes.com\", :consumer {:site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :url-referrer \"http://www.twitter.com\", :ip-address \"64.9.247.255\"}, :rmc-cookies {:rmc-1 \"foo\"}, :search-terms (\":donut\" \":eggos\"), :merchant {:id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :vertical \"high\", :platform \"some platform\"}, :geo-data {:ipinfo {:location {:state-data {:state-code \"RI\"}, :country-data {:country-code \"USA\"}}}}, :client-info {:runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :ip-address \"64.9.247.255\", :browser {:name \"Chrome\", :rendering-engine \"re\"}, :os {:name \"Windows 7\"}, :device {:mobile? false, :type \"laptop\"}}}, :runa-enabled true, :merchant-id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :event-id \"123450000:abcdefgh\", :event-type :session-data, :git-sha \"f43283cd56fb1e2dcf4c992aad79f050fe345ba5\", :session-id \"e5b03c68-318f-4072-a626-e3f423a2b333\", :version 1, :site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\"} {:timestamp 123450000, :data {:paid-ad true, :timezone \"EST\", :sushi true, :offer-model :random, :baseline-info {:enabled true, :starts-on 1231000, :percentage 50.0, :offer true}, :url-referrer \"http://www.twitter.com\", :bucket 123, :paid-search-terms (\":apple\" \":banana\" \":cherry\"), :session-start-time 12412444, :landing-url \"http://www.shoes.com\", :consumer {:site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :url-referrer \"http://www.twitter.com\", :ip-address \"64.9.247.255\"}, :rmc-cookies {:rmc-1 \"foo\"}, :search-terms (\":donut\" \":eggos\"), :merchant {:id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :vertical \"high\", :platform \"some platform\"}, :geo-data {:ipinfo {:location {:state-data {:state-code \"RI\"}, :country-data {:country-code \"USA\"}}}}, :client-info {:runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :ip-address \"64.9.247.255\", :browser {:name \"Chrome\", :rendering-engine \"re\"}, :os {:name \"Windows 7\"}, :device {:mobile? false, :type \"laptop\"}}}, :runa-enabled true, :merchant-id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :event-id \"123450000:abcdefgh\", :event-type :session-data, :git-sha \"f43283cd56fb1e2dcf4c992aad79f050fe345ba5\", :session-id \"e5b03c68-318f-4072-a626-e3f423a2b333\", :version 1, :site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\"}))

FAIL in (test-migrate) (migration_spec.clj:859)
session data events
expected: (= session-data-event---missing-data (migrate legacy-session-data-event---missing-data))
  actual: (not (= {:timestamp 123450000, :data {:paid-ad true, :timezone \"EST\", :sushi nil, :offer-model :random, :baseline-info {:enabled true, :starts-on 1231231, :percentage 50.0, :offer true}, :url-referrer nil, :bucket 123, :paid-search-terms [], :session-start-time 12412444, :landing-url \"http://www.shoes.com\", :consumer {:site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid nil, :url-referrer nil, :ip-address \"64.9.247.255\"}, :search-terms [], :merchant {:id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :vertical \"high\", :platform \"some platform\"}, :geo-data {:ipinfo {:location {:state-data {:state-code \"RI\"}, :country-data {:country-code \"USA\"}}}, :extr-geo-data {:temperature {:amount 55, :unit \"F\"}}}, :client-info {:runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :ip-address \"64.9.247.255\", :browser {:name \"Chrome\", :rendering-engine nil}, :device {:mobile? false, :type nil}}}, :runa-enabled true, :merchant-id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :event-id \"123450000:abcdefgh\", :event-type :session-data, :git-sha \"f43283cd56fb1e2dcf4c992aad79f050fe345ba5\", :session-id \"e5b03c68-318f-4072-a626-e3f423a2b333\", :version 1, :site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid nil} {:timestamp 123450000, :data {:paid-ad true, :timezone \"EST\", :sushi nil, :offer-model :random, :baseline-info {:enabled true, :starts-on 1231000, :percentage 50.0, :offer true}, :url-referrer nil, :bucket 123, :paid-search-terms (), :session-start-time 12412444, :landing-url \"http://www.shoes.com\", :consumer {:site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid nil, :url-referrer nil, :ip-address \"64.9.247.255\"}, :search-terms (), :merchant {:id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :vertical \"high\", :platform \"some platform\"}, :geo-data {:ipinfo {:location {:state-data {:state-code \"RI\"}, :country-data {:country-code \"USA\"}}}, :extr-geo-data {:temperature {:amount 55, :unit \"F\"}}}, :client-info {:runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :ip-address \"64.9.247.255\", :browser {:name \"Chrome\", :rendering-engine nil}, :device {:mobile? false, :type nil}}}, :runa-enabled true, :merchant-id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :event-id \"123450000:abcdefgh\", :event-type :session-data, :git-sha \"f43283cd56fb1e2dcf4c992aad79f050fe345ba5\", :session-id \"e5b03c68-318f-4072-a626-e3f423a2b333\", :version 1, :site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid nil}))

FAIL in (regression--baseline-info-schemas-were-too-strict) (migration_spec.clj:951)
expected: (= migrated-oscaro-session-data-event (migrate legacy-oscaro-session-data-event))
  actual: (not (= {:timestamp 1343799567304, :data {:paid-ad false, :sushi nil, :baseline-info {:offer true, :starts-on 1343737980000, :enabled true, :percentage 15.4}, :url-referrer nil, :paid-search-terms [], :session-start-time 1343799567303, :landing-url nil, :consumer {:url-referrer nil, :ip-address \"50.131.104.242\", :runa-cid \"49062b00-ba24-4971-a453-2d511854e409\", :site-cid \"sid-41840352-db9b-11e1-b346-f3dcb85b63ab\"}, :rmc-cookies {:rmc-1 \"1\"}, :search-terms [], :merchant {:id \"74992639-fec6-3187-b23b-be27eea1c919\", :vertical \"retailer\", :platform \"other\"}, :geo-data nil, :client-info {:device {:mobile? false, :type \"Computer\"}, :os {:name \"Mac OS X\"}, :browser {:name \"Firefox\", :rendering-engine \"GECKO\"}, :ip-address \"50.131.104.242\", :runa-cid \"619eaf35-1dda-4869-8a93-b18877bbdee7\"}}, :runa-enabled false, :merchant-id \"74992639-fec6-3187-b23b-be27eea1c919\", :event-id \"1343799567304:4b194b81-a0d0-41d8-b25b-761a805b039f\", :event-type :session-data, :git-sha \"3dcb6dbae72a1598d9cd160d523504091fdb633b\", :session-id \"4b194b81-a0d0-41d8-b25b-761a805b039f\", :version 1, :site-cid \"sid-41840352-db9b-11e1-b346-f3dcb85b63ab\", :runa-cid nil} {:timestamp 1343799567304, :data {:paid-ad false, :sushi nil, :baseline-info {:offer true, :starts-on \"2012-07-31 12:33\", :enabled true, :percentage 15.4}, :url-referrer nil, :paid-search-terms [], :session-start-time 1343799567303, :landing-url nil, :consumer {:url-referrer nil, :ip-address \"50.131.104.242\", :runa-cid \"49062b00-ba24-4971-a453-2d511854e409\", :site-cid \"sid-41840352-db9b-11e1-b346-f3dcb85b63ab\"}, :rmc-cookies {:rmc-1 \"1\"}, :search-terms [], :merchant {:id \"74992639-fec6-3187-b23b-be27eea1c919\", :vertical \"retailer\", :platform \"other\"}, :geo-data nil, :client-info {:device {:mobile? false, :type \"Computer\"}, :os {:name \"Mac OS X\"}, :browser {:name \"Firefox\", :rendering-engine \"GECKO\"}, :ip-address \"50.131.104.242\", :runa-cid \"619eaf35-1dda-4869-8a93-b18877bbdee7\"}}, :runa-enabled false, :merchant-id \"74992639-fec6-3187-b23b-be27eea1c919\", :event-id \"1343799567304:4b194b81-a0d0-41d8-b25b-761a805b039f\", :event-type :session-data, :git-sha \"3dcb6dbae72a1598d9cd160d523504091fdb633b\", :session-id \"4b194b81-a0d0-41d8-b25b-761a805b039f\", :version 1, :site-cid \"sid-41840352-db9b-11e1-b346-f3dcb85b63ab\", :runa-cid nil}))

Ran 5 tests containing 36 assertions.
3 failures, 0 errors.")


(def regression-string-2
  "
:starts-on \"1970-01-01 00:20:31\",
:percentage 50.0,
:offer true}


Testing furtive.serenity.migration-spec

FAIL in (test-migrate) (migration_spec.clj:859)
session data events
expected: (= session-data-event (migrate legacy-session-data-event))
  actual: (not (= {:timestamp 123450000, :data {:paid-ad true, :timezone \"EST\", :sushi true, :offer-model :random, :baseline-info {:enabled true, :starts-on 1231231, :percentage 50.0, :offer true}, :url-referrer \"http://www.twitter.com\", :bucket 123, :paid-search-terms [\":apple\" \":banana\" \":cherry\"], :session-start-time 12412444, :landing-url \"http://www.shoes.com\", :consumer {:site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :url-referrer \"http://www.twitter.com\", :ip-address \"64.9.247.255\"}, :rmc-cookies {:rmc-1 \"foo\"}, :search-terms (\":donut\" \":eggos\"), :merchant {:id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :vertical \"high\", :platform \"some platform\"}, :geo-data {:ipinfo {:location {:state-data {:state-code \"RI\"}, :country-data {:country-code \"USA\"}}}}, :client-info {:runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :ip-address \"64.9.247.255\", :browser {:name \"Chrome\", :rendering-engine \"re\"}, :os {:name \"Windows 7\"}, :device {:mobile? false, :type \"laptop\"}}}, :runa-enabled true, :merchant-id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :event-id \"123450000:abcdefgh\", :event-type :session-data, :git-sha \"f43283cd56fb1e2dcf4c992aad79f050fe345ba5\", :session-id \"e5b03c68-318f-4072-a626-e3f423a2b333\", :version 1, :site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\"} {:timestamp 123450000, :data {:paid-ad true, :timezone \"EST\", :sushi true, :offer-model :random, :baseline-info {:enabled true, :starts-on 1231000, :percentage 50.0, :offer true}, :url-referrer \"http://www.twitter.com\", :bucket 123, :paid-search-terms (\":apple\" \":banana\" \":cherry\"), :session-start-time 12412444, :landing-url \"http://www.shoes.com\", :consumer {:site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :url-referrer \"http://www.twitter.com\", :ip-address \"64.9.247.255\"}, :rmc-cookies {:rmc-1 \"foo\"}, :search-terms (\":donut\" \":eggos\"), :merchant {:id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :vertical \"high\", :platform \"some platform\"}, :geo-data {:ipinfo {:location {:state-data {:state-code \"RI\"}, :country-data {:country-code \"USA\"}}}}, :client-info {:runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :ip-address \"64.9.247.255\", :browser {:name \"Chrome\", :rendering-engine \"re\"}, :os {:name \"Windows 7\"}, :device {:mobile? false, :type \"laptop\"}}}, :runa-enabled true, :merchant-id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :event-id \"123450000:abcdefgh\", :event-type :session-data, :git-sha \"f43283cd56fb1e2dcf4c992aad79f050fe345ba5\", :session-id \"e5b03c68-318f-4072-a626-e3f423a2b333\", :version 1, :site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\"}))

FAIL in (test-migrate) (migration_spec.clj:859)
session data events
expected: (= session-data-event---missing-data (migrate legacy-session-data-event---missing-data))
  actual: (not (= {:timestamp 123450000, :data {:paid-ad true, :timezone \"EST\", :sushi nil, :offer-model :random, :baseline-info {:enabled true, :starts-on 1231231, :percentage 50.0, :offer true}, :url-referrer nil, :bucket 123, :paid-search-terms [], :session-start-time 12412444, :landing-url \"http://www.shoes.com\", :consumer {:site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid nil, :url-referrer nil, :ip-address \"64.9.247.255\"}, :search-terms [], :merchant {:id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :vertical \"high\", :platform \"some platform\"}, :geo-data {:ipinfo {:location {:state-data {:state-code \"RI\"}, :country-data {:country-code \"USA\"}}}, :extr-geo-data {:temperature {:amount 55, :unit \"F\"}}}, :client-info {:runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :ip-address \"64.9.247.255\", :browser {:name \"Chrome\", :rendering-engine nil}, :device {:mobile? false, :type nil}}}, :runa-enabled true, :merchant-id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :event-id \"123450000:abcdefgh\", :event-type :session-data, :git-sha \"f43283cd56fb1e2dcf4c992aad79f050fe345ba5\", :session-id \"e5b03c68-318f-4072-a626-e3f423a2b333\", :version 1, :site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid nil} {:timestamp 123450000, :data {:paid-ad true, :timezone \"EST\", :sushi nil, :offer-model :random, :baseline-info {:enabled true, :starts-on 1231000, :percentage 50.0, :offer true}, :url-referrer nil, :bucket 123, :paid-search-terms (), :session-start-time 12412444, :landing-url \"http://www.shoes.com\", :consumer {:site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid nil, :url-referrer nil, :ip-address \"64.9.247.255\"}, :search-terms (), :merchant {:id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :vertical \"high\", :platform \"some platform\"}, :geo-data {:ipinfo {:location {:state-data {:state-code \"RI\"}, :country-data {:country-code \"USA\"}}}, :extr-geo-data {:temperature {:amount 55, :unit \"F\"}}}, :client-info {:runa-cid \"e5b03c68-318f-4072-a626-e3f423a2b444\", :ip-address \"64.9.247.255\", :browser {:name \"Chrome\", :rendering-engine nil}, :device {:mobile? false, :type nil}}}, :runa-enabled true, :merchant-id \"e5b03c68-318f-4072-a626-e3f423a2b222\", :event-id \"123450000:abcdefgh\", :event-type :session-data, :git-sha \"f43283cd56fb1e2dcf4c992aad79f050fe345ba5\", :session-id \"e5b03c68-318f-4072-a626-e3f423a2b333\", :version 1, :site-cid \"e5b03c68-318f-4072-a626-e3f423a2b555\", :runa-cid nil}))

FAIL in (regression--baseline-info-schemas-were-too-strict) (migration_spec.clj:951)
expected: (= migrated-oscaro-session-data-event (migrate legacy-oscaro-session-data-event))
  actual: (not (= {:timestamp 1343799567304, :data {:paid-ad false, :sushi nil, :baseline-info {:offer true, :starts-on 1343737980000, :enabled true, :percentage 15.4}, :url-referrer nil, :paid-search-terms [], :session-start-time 1343799567303, :landing-url nil, :consumer {:url-referrer nil, :ip-address \"50.131.104.242\", :runa-cid \"49062b00-ba24-4971-a453-2d511854e409\", :site-cid \"sid-41840352-db9b-11e1-b346-f3dcb85b63ab\"}, :rmc-cookies {:rmc-1 \"1\"}, :search-terms [], :merchant {:id \"74992639-fec6-3187-b23b-be27eea1c919\", :vertical \"retailer\", :platform \"other\"}, :geo-data nil, :client-info {:device {:mobile? false, :type \"Computer\"}, :os {:name \"Mac OS X\"}, :browser {:name \"Firefox\", :rendering-engine \"GECKO\"}, :ip-address \"50.131.104.242\", :runa-cid \"619eaf35-1dda-4869-8a93-b18877bbdee7\"}}, :runa-enabled false, :merchant-id \"74992639-fec6-3187-b23b-be27eea1c919\", :event-id \"1343799567304:4b194b81-a0d0-41d8-b25b-761a805b039f\", :event-type :session-data, :git-sha \"3dcb6dbae72a1598d9cd160d523504091fdb633b\", :session-id \"4b194b81-a0d0-41d8-b25b-761a805b039f\", :version 1, :site-cid \"sid-41840352-db9b-11e1-b346-f3dcb85b63ab\", :runa-cid nil} {:timestamp 1343799567304, :data {:paid-ad false, :sushi nil, :baseline-info {:offer true, :starts-on \"2012-07-31 12:33\", :enabled true, :percentage 15.4}, :url-referrer nil, :paid-search-terms [], :session-start-time 1343799567303, :landing-url nil, :consumer {:url-referrer nil, :ip-address \"50.131.104.242\", :runa-cid \"49062b00-ba24-4971-a453-2d511854e409\", :site-cid \"sid-41840352-db9b-11e1-b346-f3dcb85b63ab\"}, :rmc-cookies {:rmc-1 \"1\"}, :search-terms [], :merchant {:id \"74992639-fec6-3187-b23b-be27eea1c919\", :vertical \"retailer\", :platform \"other\"}, :geo-data nil, :client-info {:device {:mobile? false, :type \"Computer\"}, :os {:name \"Mac OS X\"}, :browser {:name \"Firefox\", :rendering-engine \"GECKO\"}, :ip-address \"50.131.104.242\", :runa-cid \"619eaf35-1dda-4869-8a93-b18877bbdee7\"}}, :runa-enabled false, :merchant-id \"74992639-fec6-3187-b23b-be27eea1c919\", :event-id \"1343799567304:4b194b81-a0d0-41d8-b25b-761a805b039f\", :event-type :session-data, :git-sha \"3dcb6dbae72a1598d9cd160d523504091fdb633b\", :session-id \"4b194b81-a0d0-41d8-b25b-761a805b039f\", :version 1, :site-cid \"sid-41840352-db9b-11e1-b346-f3dcb85b63ab\", :runa-cid nil}))

Ran 5 tests containing 36 assertions.
3 failures, 0 errors.")


(deftest test-regression
  (is (= 3
         (count (ct-report-str->failure-maps regression-string-2)))))



;; TODO: test exps/acts/ with differenet lengths/heights space properly in the
;; diff report



