(ns such.f-metadata
  (:require [such.metadata :as meta])
  (:use midje.sweet))


(def o (with-meta [] {:meta :here}))

(fact "get avoids needing to use `meta`"
  (get (meta o) :meta) => :here
  (meta/get o :meta) => :here

  (get (meta o) :unknown) => nil
  (meta/get o :unknown) => nil

  (get (meta o) :unknown "default") => "default"
  (meta/get o :unknown "default") => "default")

(fact "merge avoids needing to use `meta`"
  (let [oo (meta/merge o {:a 1})]
    (meta oo) => {:meta :here, :a 1}
    (= oo o) => true)

  (fact "multiple arguments"
    (let [oo (meta/merge o {:a 1} {:b 2})]
      (meta oo) => {:meta :here, :a 1 :b 2}))

  (fact "no arguments are required"
    (let [oo (meta/merge o)]
      (meta oo) => (meta o)
      (identical? oo o) => false)))

(fact "assoc avoids needing to use `meta`"
  (let [oo (meta/assoc o :a 1)]
    (meta oo) => {:meta :here, :a 1}
    (= oo o) => true)

  (fact "multiple arguments"
    (let [oo (meta/assoc o :a 1 :b 2)]
      (meta oo) => {:meta :here, :a 1 :b 2}))

  (fact "no arguments are required"
    (let [oo (meta/assoc o)]
      (meta oo) => (meta o)
      (identical? oo o) => false)))

(fact "contains?"
  (meta/contains? o :meta) => true
  (meta/contains? o :not-meta) => false
  (meta/contains? (with-meta [] {:protocol nil}) :protocol) => true)
