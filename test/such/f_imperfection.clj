(ns such.f-imperfection
  (:require [such.imperfection :as subject])
  (:use midje.sweet))

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
    s => "hi\n[1 :a]\n")
  (fact "accepts values other than strings"
    (let [[_ s] (subject/val-and-output (subject/tag- 'hi 1))]
      s => "hi\n")
    (let [[_ s] (subject/val-and-output (subject/tag- :hi 1))]
      s => ":hi\n")))
    
(fact -tag
  (let [[val s] (subject/val-and-output (-> [1 :a] (subject/-tag "hi %s" 1) x-prn-))]
    val => [1 :a]
    s => "hi 1\n[1 :a]\n")
  (fact "accepts values other than strings"
    (let [[val s] (subject/val-and-output (subject/-tag :input :hi))]
      val => :input
      s => ":hi\n"))
  (fact "for non-string case, further arguments are ignored"
    (let [[val s] (subject/val-and-output (subject/-tag :input :hi 33))]
      val => :input
      s => ":hi\n")))

