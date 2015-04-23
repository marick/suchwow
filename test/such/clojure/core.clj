(ns such.clojure.core
  "This namespace demonstrates the creation of a 'catch-all' namespace that contains
   the vars from several other namespaces *and* can make those vars available to
   other namespaces that `:use` or `:require :refer` to this one."
  (:require [such.immigration :as immigrate]
            such.shorthand
            such.types
            clojure.string)
  (:use midje.sweet))

(immigrate/namespaces 'such.shorthand 'such.types)

(immigrate/selection 'such.immigration '[move-var! selection])

(fact "you can immigrate a private var, but it remains private (see f-core for confirmation)"
  (meta #'move-var!) => (contains {:private true}))

(selection 'clojure.set '[union])

(immigrate/prefixed 'clojure.string "str-")

;; It is safe to immigrate twice
(selection 'clojure.set '[union intersection])


;; Demonstrate handling of existing ns-map entries.

(fact
  (contains? (ns-map *ns*) 'root-value) => false
  (contains? (ns-map *ns*) 'has-root-value?) => false)

(def root-value "this value will be overwritten by the version from such.vars.")
;; ... as will this referred value
(require '[such.vars :refer [has-root-value?]])

(fact
  (contains? (ns-map *ns*) 'root-value) => true
  (contains? (ns-interns *ns*) 'root-value) => true
  (contains? (ns-refers *ns*) 'root-value) => false

  (contains? (ns-map *ns*) 'has-root-value?) => true
  (contains? (ns-interns *ns*) 'has-root-value?) => false
  (contains? (ns-refers *ns*) 'has-root-value?) => true)

(immigrate/namespaces 'such.vars)

(fact "the interned value is interned - but with a different value"
  (contains? (ns-map *ns*) 'root-value) => true
  (contains? (ns-interns *ns*) 'root-value) => true
  (contains? (ns-refers *ns*) 'root-value) => false
  ((find-var *ns* 'root-value) 'root-value) =not=> string?)

(fact "the referred value is now interned"
  (contains? (ns-map *ns*) 'has-root-value?) => true
  (contains? (ns-interns *ns*) 'has-root-value?) => true
  (contains? (ns-refers *ns*) 'has-root-value?) => false)
