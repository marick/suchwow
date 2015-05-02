(ns such.clojure.overwrites-client
  (:use such.clojure.overwrites)
  (:use midje.sweet))

(fact "overwriting an interned variable worked"
  (third [1 2 3]) => 3
  ((ns-refers *ns*) 'third) => #'such.clojure.overwrites/third)

(fact "the referred value was overwritten"
  ((ns-refers *ns*) 'namespace?) => #'such.clojure.overwrites/namespace?
  (find-var 'such.clojure.overwrites-client/namespace?) => nil)

