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


;;; What vars point to

(defn a-fun [n] (inc n))
(defmulti a-multi identity)
(defmacro a-macro [n] `(+ ~n ~n))
(def a-thing 5)

(defprotocol P
  (a-proto-fn [this]))

(defrecord R [a]
  P
  (a-proto-fn [this] a))

(fact 
  (var/has-macro? #'cons) => false
  (var/has-macro? #'cond) => true
  (var/has-macro? #'a-fun) => false
  (var/has-macro? #'a-multi) => false
  (var/has-macro? #'a-macro) => true
  (var/has-macro? #'a-thing) => false
  (var/has-macro? #'a-proto-fn) => false

  (var/has-function? #'cons) => true
  (var/has-function? #'cond) => false
  (var/has-function? #'a-fun) => true
  (var/has-function? #'a-multi) => true
  (var/has-function? #'a-macro) => false
  (var/has-function? #'a-thing) => false
  (var/has-function? #'a-proto-fn) => true

  (var/has-plain-value? #'cons) => false
  (var/has-plain-value? #'cond) => false
  (var/has-plain-value? #'a-fun) => false
  (var/has-plain-value? #'a-multi) => false
  (var/has-plain-value? #'a-macro) => false
  (var/has-plain-value? #'a-thing) => true
  (var/has-plain-value? #'a-proto-fn) => false)



  
