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


(fact "not-empty?"
  (subject/not-empty? [1]) => true
  (subject/not-empty? []) => false
  (subject/not-empty? (cons 1 '())) => true
  (subject/not-empty? '()) => false
  (subject/not-empty? nil) => false
  (subject/not-empty? (range 0)) => false
  (subject/not-empty? (range 1)) => true
  (subject/not-empty? (next (next (range 2)))) => false
  (subject/not-empty? (rest (rest (range 2)))) => false
  (subject/not-empty? (next (next (range 3)))) => true
  (subject/not-empty? (rest (rest (range 3)))) => true

  (subject/not-empty? "") => false
  (subject/not-empty? "1") => true
  
  (subject/not-empty? (byte-array 0)) => false
  (subject/not-empty? (byte-array 1)) => true

  (subject/not-empty? 1) => (throws))


(fact "third"
  (subject/third [1 2 3]) => 3
  (subject/third [1 2]) => nil)

(fact "fourth"
  (subject/fourth [1 2 3 4]) => 4
  (subject/fourth [1 2 3]) => nil)

(fact "find-first"
  (subject/find-first even? [1 3 4 6]) => 4
  (subject/find-first even? [3 5]) => nil
  (subject/find-first even? nil) => nil
  (subject/find-first :key {:key "value"}) => nil
  (subject/find-first #(= :key (first %)) {:key "value"}) => [:key "value"]
  (subject/find-first #{1 2} [3 2 1]) => 2
  (subject/find-first even? (range)) => 0)

