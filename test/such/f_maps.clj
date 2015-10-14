(ns such.f-maps
  (:require [such.maps :as subject]
            [such.versions :refer [when=1-6]])
  (:use midje.sweet))


;;; Maps

(facts "you can tack new keys onto a hashmap"
  (subject/conj-into {}) => {}
  (subject/conj-into {:a 1}) => {:a 1}

  (subject/conj-into {} :a 1) => '{:a (1)}
  (subject/conj-into {} :a 1) => (just {:a list?})

  (subject/conj-into {:a [1] :b '(1)} :a 2 :b 2) => '{:a [1 2] :b (2 1)}
  (subject/conj-into {:a [1] :b '(1)} :a 2 :b 2) => (just {:a vector? :b list?})

  (subject/conj-into {:a [1], :b [55] :c 'blah} :a 2 :b 56) => {:a [1 2], :b [55 56], :c 'blah})

(fact "key-difference"
  (subject/key-difference {} {}) => {}
  (subject/key-difference {:a 1} {}) => {:a 1}
  (subject/key-difference {:a 1} {:a ..irrelevant..}) => {}
  (subject/key-difference {} {:a ..irrelevant..}) => {}

  (subject/key-difference {:a 1, :b 2, :c 3} {:a ..irrelevant.., :c ..irrelevant.., :d ..irrelevant..}) => {:b 2})



(fact "invert"
  (subject/invert {:a 1, :b 2}) => {1 :a, 2 :b})

(fact "dissoc-keypath"
  (fact "removes a key/value pair"
    (subject/dissoc-keypath {:by-name {:name1 1, :name2 2}} [:by-name :name1])
    =>              {:by-name {          :name2 2}}
    (subject/dissoc-keypath {:by-name {:name1 1}} [:by-name :name1])
    =>              {:by-name {        }}
    (subject/dissoc-keypath {"1" {"2" {"3.1" 3, "3.2" 3}}} ["1" "2" "3.1"])
    =>              {"1" {"2" {         "3.2" 3}}})
  (fact "leaves the map alone if the last key is incorrect"
    (subject/dissoc-keypath {:by-name {:name1 1}} [:by-name :NOTFOUND])
    =>              {:by-name {:name1 1}})
  (fact "requires that the path up to the last key exists"
    (subject/dissoc-keypath {:by-name {:name1 1}} [:NOTFOUND :name1])
    =not=>          {:NOTFOUND {:name1 1}}))

(fact update-each-value
  (subject/update-each-value {} inc) => {}
  (subject/update-each-value {:a 1, :b 2} inc) => {:a 2 :b 3}
  (subject/update-each-value {:a [], :b [:b]} conj 1) => {:a [1] :b [:b 1]})

(fact "making a map with uniform keys"
  (subject/mkmap:all-keys-with-value [] 3) => {}
  (subject/mkmap:all-keys-with-value [:a] 3) => {:a 3}
  (subject/mkmap:all-keys-with-value [:a [:b]] 3) => {:a 3, [:b] 3})

(when=1-6 
 (fact "update"
   (subject/update {:a 1} :a + 5) => {:a 6})
)
