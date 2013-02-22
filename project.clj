(defproject gui-diff "0.4.0"
  :min-lein-version "2.0.0"
  :description "Visual diffing of Clojure data structures"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojars.trptcolin/sjacket "0.1.3" :exclusions [org.clojure/clojure]]
                 [ordered "1.2.0"]]
  :profiles {:1.3.0 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.4.0 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5.0 {:dependencies [[org.clojure/clojure "1.5.0-RC16"]]}}
  :aliases {"run-tests" ["with-profile" "1.3.0:1.4.0:1.5.0" "test"]})
