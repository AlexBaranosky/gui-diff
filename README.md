Visually diff Clojure data structures
-------------------------------------

Works with Mac out of the box, and with Linux using Meld or xterm diff.

# Usage:

To include as a project dependency using Leiningen: `[gui-diff "0.3.2"]`

##Examples

```clj
(require '[gui-diff.core :as gd])

;; use gui-diff's clojure.test wrappers that will popup a visual diff
;; failure report if any test fails
(gd/run-tests++ 'mylibrary.core-test)
(gd/run-all-tests++ #".*integration.*")
```

```clj
;; wrap any code that sends failure information to clojure.test's *test-out*
;; using `with-gui-diff`

(gd/with-gui-diff (my-custom-test-runner))
```

```clj
Popup a visual diff of any two data structures
(gd/gui-diff {:a 10 :b 20 :c 30} {:a 10 :c 98 :d 34})
```

## Override the diff tool

You can specify your own diff tool by setting the DIFFTOOL environment variable.

```bash
$ DIFFTOOL=kdiff3 lein repl
```


Contributors
------------
* [Alex Baranosky](https://github.com/AlexBaranosky)
* [Shantanu Kumar](https://github.com/kumarshantanu)
* [tutysara](https://github.com/tutysara)
