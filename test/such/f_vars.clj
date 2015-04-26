(ns such.f-vars
  (:require [such.vars :as var])
  (:use midje.sweet))


(def unbound)
(def ^:dynamic bound 1)
(def ^:dynamic rebound)

(fact "has-root-value?"
  (var/has-root-value? #'unbound) => false
  (var/has-root-value? #'rebound) => false
  (var/has-root-value? #'bound) => true

  (fact "bindings don't affect root value"
    (binding [rebound 3]
      rebound => 3
      (var/has-root-value? #'rebound) => false))

  (fact "requires a var"
    (var/has-root-value? 'bound) => (throws IllegalArgumentException)))

(fact "root-value"
  ;; root value of unbound value differs in Clojure versions; undefined here.
  (var/root-value #'bound) => 1

  (fact "bindings don't affect root value"
    (binding [bound 3]
      bound => 3
      (var/root-value #'bound) => 1))

  (fact "requires a var"
    (var/root-value 'bound) => (throws IllegalArgumentException)))

(fact "name-as-symbol"
  (var/name-as-symbol #'clojure.core/even?) => 'even?)


(fact "name-as-string"
  (var/name-as-string #'clojure.core/even?) => "even?")
