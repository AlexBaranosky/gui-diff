Visually diff Clojure data structures
-------------------------------------

Works with Mac out of the box, and with Linux using Meld or xterm diff.

# Usage:

To include as a project dependency using Leiningen: `[gui-diff "0.5.0"]`

##Travis CI Status

[![Build Status](https://travis-ci.org/AlexBaranosky/gui-diff.png)](https://travis-ci.org/AlexBaranosky/gui-diff)


##Examples

```clj
(require '[gui.diff :refer :all])

;; popup a visual diff of any two data structures
(gui-diff {:a 10 :b 20 :c 30} {:a 10 :c 98 :d 34})
```

```clj
;; wrap any code that sends failure information to clojure.test's *test-out*
;; using `with-gui-diff`

(with-gui-diff (my-custom-test-runner))
```

```clj
;; use gui-diff's clojure.test wrappers that will popup a visual diff
;; failure report if any test fails
(run-tests++ 'mylibrary.core-test)
(run-all-tests++ #".*integration.*")
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
