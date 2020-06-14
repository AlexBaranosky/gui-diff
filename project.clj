(defproject gui-diff "0.6.7"
  :description "Visual diffing of Clojure data structures"
  :url "https://github.com/AlexBaranosky/gui-diff"
  :dependencies [[fipp "0.4.1"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojars.trptcolin/sjacket "0.1.4" :exclusions [org.clojure/clojure]]
                 [org.codehaus.jsr166-mirror/jsr166y "1.7.0"]
                 [org.flatland/ordered "1.5.9"]]
  :profiles {:1.3.0 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.4.0 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5.1 {:dependencies [[org.clojure/clojure "1.5.1"]]}}
  :deploy-repositories [["clojars"
                         {:sign-releases false}]]
  :aliases {"run-tests" ["with-profile" "1.3.0:1.4.0:1.5.1" "test"]})
