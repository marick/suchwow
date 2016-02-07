(ns such.f-types
  (:use such.versions)
  (:require [such.types :as subject])
  (:use midje.sweet))

(facts "about stringlike objects"
  (string? "s") => true
  (string? #"s") => false
  (string? 1) => false

  (subject/regex? "s") => false
  (subject/regex? #"s") => true
  (subject/regex? 1) => false

  (subject/stringlike? "s") => true
  (subject/stringlike? #"s") => true
  (subject/stringlike? 1) => false)

(defrecord R [a])

(facts "about maps and records"
  (map? (R. 1)) => true
  (map? (hash-map)) => true
  (map? (sorted-map)) => true
  (map? 1) => false

  (subject/classic-map? (R. 1)) => false
  (subject/classic-map? (hash-map)) => true
  (subject/classic-map? (sorted-map)) => true
  (subject/classic-map? 1) => false)

(facts "about bigdecimal"
  (subject/big-decimal? 1) => false
  (subject/big-decimal? (int 1)) => false
  (subject/big-decimal? (long 1)) => false
  (subject/big-decimal? 1.0) => false
  (subject/big-decimal? 1.0M) => true)

(defmulti twofer identity)

(facts "about extended-fn"
  (fn? cons) => true
  (fn? twofer) => false

  (subject/extended-fn? cons) => true
  (subject/extended-fn? twofer) => true)

(defrecord ExampleNamed []
  clojure.lang.Named
  (getName [this] "name")
  (getNamespace [this] "namespace"))

(facts "about named objects"
  (name "foo") => "foo" ; yay!
  (name 'foo) => "foo"
  (name 'such.named) => "such.named"
  (name :foo) => "foo"
  (name ::foo) => "foo"
  (name :fake/namespace) => "namespace"
  (name (ExampleNamed.)) => "name"
  (name *ns*) => (throws) ; Boo!
  (name \c) => (throws)

  (subject/named? "foo") => true
  (subject/named? 'foo) => true
  (subject/named? 'such.subject/named) => true
  (subject/named? :foo) => true
  (subject/named? ::foo) => true
  (subject/named? :fake/namespace) => true
  (subject/named? (ExampleNamed.)) => true
  (subject/named? *ns*) => false
  (subject/named? \c) => false)

(fact "linear access"
  (subject/linear-access? []) => false
  (subject/linear-access? '(1)) => true
  (subject/linear-access? '()) => true
  (subject/linear-access? nil) => false
  (subject/linear-access? (map identity [1 2 3])) => true
  (subject/linear-access? (hash-map)) => false)

(fact "namespace?"
  (subject/namespace? *ns*) => true
  (subject/namespace? (ns-name *ns*)) => false
  (subject/namespace? "ns") => false)
