(ns such.f-symbols (:require [such.symbols :as symbol])
  (use midje.sweet))

(def d)

(fact "from-concatenation"
  (symbol/from-concatenation ['a "b" :c #'d]) => 'abcd
  (symbol/from-concatenation [:namespace/un #'clojure.core/even?]) => 'uneven?

  (symbol/from-concatenation ["a" "b"] '-) => 'a-b)


(fact "without-namespace"
  (symbol/without-namespace 'clojure.core/even?) => 'even?
  (symbol/without-namespace 'red) => 'red)
