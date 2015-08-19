(ns such.f-imperfection
  (:require [such.imperfection :as subject]
            [such.versions :refer [when>=1-7]])
  (:use midje.sweet))

(when>=1-7

;; These are kludges because the function names look like metaconstants.
(def x-pprint- subject/-pprint-)
(def x-prn- subject/-prn-)

(fact -pprint-
  (subject/val-and-output (-> [1 :a] x-pprint-)) => (just [1 :a] #"\[1 :a\]"))

(fact -prn-
  (subject/val-and-output (-> [1 :a] x-prn-)) => (just [1 :a] #"\[1 :a\]"))

(fact tag-
  (let [[val s] (subject/val-and-output (->> [1 :a] (subject/tag- "hi") x-prn-))]
    val => [1 :a]
    s => #"hi"
    s => #"\[1 :a\]"))


(fact -tag
  (let [[val s] (subject/val-and-output (-> [1 :a] (subject/-tag "hi %s" 1) x-prn-))]
    val => [1 :a]
    s => #"hi 1"
    s => #"\[1 :a\]"))

) ; when>=1-7
