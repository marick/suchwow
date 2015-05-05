(ns such.wide-domains
  "Variants of clojure.core functions that accept more types of inputs.
   This is a catch-all namespace that collects core-overriding functions
   from other namespaces. It has two purposes:
   
   * to show you all of such functions on one page of documentation.
   * to let you immigrate all of them in one swell foop.
   
   See [`such.clojure.core`](https://github.com/marick/suchwow/blob/master/test/such/clojure/core.clj) for an example.
"
  (:require [such.immigration :as immigrate]))

(immigrate/selection 'such.ns '[+find-var])
(immigrate/selection 'such.symbols '[+symbol])

