(ns such.clojure.f-overwrites
  (:use such.clojure.overwrites)
  (:use midje.sweet))

(fact "overwriting an interned variable works"
  (third [1 2 3]) => 3
  ((ns-refers *ns*) 'third) => #'such.clojure.overwrites/third)

(fact "the referred value was not made available"
  ((ns-refers *ns*) 'namespace?) => nil
  (find-var 'such.clojure.f-overwrites/namespace?) => nil)

