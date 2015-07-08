(ns such.f-function-makers
  (:require [such.function-makers :as mkfn])
  (:use midje.sweet))


(fact "any-pred"
  ((mkfn/any-pred odd? even?) 1) => true
  ((mkfn/any-pred pos? neg?) 0) => false
  ((mkfn/any-pred :key :word) {:key false}) => false
  ((mkfn/any-pred :key :word) {:key false :word 3}) => true
  ((mkfn/any-pred #{1 2} #{3 4}) 3) => true
  ;; stops at first match
  ((mkfn/any-pred (partial = 3) (fn[_](throw (new Error "boom!")))) 3) => true
  ;; Any empty list means that everything matches
  ((mkfn/any-pred) 3) => true)

