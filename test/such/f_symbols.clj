(ns such.f-symbols (:require [such.symbols :as symbol])
  (:use midje.sweet))

(def d)

(fact symbol
  (symbol/+symbol "th") => 'th
  (symbol/+symbol #'clojure.core/even?) => 'even?
  (symbol/+symbol *ns* "th2") => 'such.f-symbols/th2
  (symbol/+symbol 'such.f-symbols "th3") => 'such.f-symbols/th3
  (symbol/+symbol "such.f-symbols" "th4") => 'such.f-symbols/th4
  (symbol/+symbol "no.such.namespace" "th5") => 'no.such.namespace/th5
  (symbol/+symbol *ns* 'th6) => 'such.f-symbols/th6
  (symbol/+symbol *ns* :th7) => 'such.f-symbols/th7)

(fact "from-concatenation"
  (symbol/from-concatenation ['a "b" :c #'d]) => 'abcd
  (symbol/from-concatenation [:namespace/un #'clojure.core/even?]) => 'uneven?

  (symbol/from-concatenation ["a" "b"] '-) => 'a-b)


(fact "without-namespace"
  (symbol/without-namespace 'clojure.core/even?) => 'even?
  (symbol/without-namespace 'red) => 'red)
