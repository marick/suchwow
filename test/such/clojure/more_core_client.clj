(ns such.clojure.more-core-client
  (:use midje.sweet)
  (:require [such.immigration :as immigrate]))
;; Because `more-core` redefines `clojure.core/symbol`, etc., it's
;; accessed via the following. It's unfortunate that this
;; "declaration" is outside the `ns` construct, but if we had it
;; inside, we'd have to individually `:refer-clojure :exclude` each of
;; the overridden symbols, which is both annoying and would have to be
;; updated every time a new `suchwow` version was used.

(immigrate/namespaces-by-reference 'such.clojure.more-core)

(fact "shorthand and types are available, as in `such.clojure.core` case"
  ((ns-refers *ns*) 'fourth) => #'such.clojure.more-core/fourth
  (namespace? *ns*) => true)

(fact "wider domains are present"
  ((ns-refers *ns*) 'find-var) => #'such.clojure.more-core/find-var
  (symbol *ns* :from-keyword) => 'such.clojure.more-core-client/from-keyword)


