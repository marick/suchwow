(ns such.wide-domains
  "Variants of clojure.core functions that accept more types of inputs.
   This is a catch-all namespace that collects core-overriding functions
   from other namespaces"
  (:require [such.immigration :as immigrate]))

(immigrate/selection 'such.ns '[find-var])
(immigrate/selection 'such.symbols '[symbol])

