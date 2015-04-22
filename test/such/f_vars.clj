(ns such.f-vars
  (:require [such.vars :as subject])
  (:use midje.sweet))


(def unbound)
(def ^:dynamic bound 1)
(def ^:dynamic rebound)

(fact "has-root-value?"
  (subject/has-root-value? #'unbound) => false
  (subject/has-root-value? #'rebound) => false
  (subject/has-root-value? #'bound) => true

  (fact "bindings don't affect root value"
    (binding [rebound 3]
      rebound => 3
      (subject/has-root-value? #'rebound) => false))

  (fact "requires a var"
    (subject/has-root-value? 'bound) => (throws IllegalArgumentException)))

(fact "root-value"
  ;; root value of unbound value differs in Clojure versions; undefined here.
  (subject/root-value #'bound) => 1

  (fact "bindings don't affect root value"
    (binding [bound 3]
      bound => 3
      (subject/root-value #'bound) => 1))

  (fact "requires a var"
    (subject/root-value 'bound) => (throws IllegalArgumentException)))
