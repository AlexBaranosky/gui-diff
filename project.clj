(defproject gui-diff "0.6.3"
  :min-lein-version "2.0.0"
  :description "Visual diffing of Clojure data structures"
  :dependencies [[fipp "0.4.0"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojars.trptcolin/sjacket "0.1.3" :exclusions [org.clojure/clojure]]
                 [org.codehaus.jsr166-mirror/jsr166y "1.7.0"]
                 [ordered "1.2.0"]]
  :profiles {:1.3.0 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.4.0 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5.1 {:dependencies [[org.clojure/clojure "1.5.1"]]}}
  :aliases {"run-tests" ["with-profile" "1.3.0:1.4.0:1.5.1" "test"]})
