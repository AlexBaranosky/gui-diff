Visually diff Clojure data structures
-------------------------------------

Works with Mac out of the box, and with Linux using Meld or xterm diff.

# Usage:

To include as a project dependency using Leiningen: `[gui-diff "0.2.0"]`

## Example

```bash
$ lein repl
```
```clojure
(require '[gui-diff.core :as gd])
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
