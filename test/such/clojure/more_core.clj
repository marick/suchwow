(ns such.clojure.more-core
  "This namespace augments the demonstration in such.clojure.core by showing how to
   handle suchwow functions that override ones used in Clojure (ones with 
   wider domains). It's somewhat dodgy to use unqualified names 
   to refer to functions that a reader would be justified in expecting to be the
   `clojure.core` version. But if you want to dodge, here's how.

   See such.clojure.more-core-client to see how this namespace is used."
  (:require [such.immigration :as immigrate]
            ;; the following updates docstrings for clojure.core
            such.better-doc))

;; As in `such.clojure.core`
(immigrate/namespaces 'such.shorthand 'such.types)
(immigrate/selection 'such.immigration '[move-var! selection])
(immigrate/prefixed 'clojure.string "str-")

;; Now we immigrate our improvements on `clojure.core` functions:
(immigrate/namespaces 'such.wide-domains)
