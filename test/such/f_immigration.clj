(ns such.f-immigration (:require [such.immigration :as immigrate])
  (:use midje.sweet))

;; Most tests of behavior are in `such.clojure.core`. This is about
;; checking that the input domain is as wide as advertised. 

(immigrate/namespaces "such.casts")

(fact as-symbol-without-namespace => fn?)
(immigrate/namespaces (find-ns 'such.immigration))
(fact namespaces => fn?)

(immigrate/selection (find-ns 'such.types) ['regex? "stringlike?" #'such.types/classic-map?])
(facts
  regex? => fn?
  stringlike? => fn?
  classic-map? => fn?)

(immigrate/prefixed 'clojure.string 'str-)
(fact str-join => fn?)

(immigrate/prefixed (find-ns 'clojure.string) "str-2-")
(fact str-2-join => fn?)

(immigrate/prefixed "clojure.string" ::str-3-)
(fact str-3-join => fn?)

