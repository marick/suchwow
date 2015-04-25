(ns such.f-shorthand (:require [such.shorthand :as subject])
  (use midje.sweet))

(facts "any?"
  (fact "first arg is a function"
    (subject/any? even? [1 2 3]) => true
    (subject/any? even? [1 3]) => false
    (subject/any? inc [1 2 3]) => true
    (subject/any? identity [nil false]) => false)

  (fact "first arg is a collection"
    (subject/any? #{1 3} [5 3 1]) => true
    (subject/any? #{1 3} [5 "a" "b"]) => false
    (subject/any? [1 3] [5 3 1]) => true
    (subject/any? [1 3] [5 "a" "b"]) => false

    (subject/any? {:a 1} {:a 1}) => true
    (subject/any? {:a 1} {:a 2}) => false
    (subject/any? {:a 1} {:b 1}) => false
    (subject/any? {:a 2, :b 1} {:b 1, :c 3}) => true)
    
  (fact "first arg is a keyword"
    (contains? {:a 1, :b 2} :a) => true
    (subject/any? :a {:a 1, :b 2}) => true
    (subject/any? :a {:b 1}) => false))
