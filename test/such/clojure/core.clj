(ns such.clojure.core
  "This namespace demonstrates the creation of a 'catch-all' namespace that contains
   the vars from several other namespaces *and* can make those vars available to
   other namespaces that `:use` or `:require :refer` to this one."
  (:require [such.immigration :as immigrate]
            such.shorthand
            such.types
            clojure.string)
  (:use midje.sweet))

;; Here are the three ways to immigrate:
;; 1. Every public var in a set of namespaces
(immigrate/namespaces 'such.shorthand 'such.types)
;; 2. Selected vars in a single namespace
(immigrate/selection 'such.immigration '[move-var! selection])
;; 3. Every public var in a namespace, with the resulting vars in this namespace prefixed.
(immigrate/prefixed 'clojure.string "str-")
;; Go to the f_core.clj file in this directory to see use of this namespace.


;; Other possibly interesting facts

(fact "you can immigrate a private var, but it remains private (see f-core for confirmation)"
  (meta #'move-var!) => (contains {:private true}))



;; It is safe to immigrate twice. 
(selection 'clojure.set '[union]) ; Note that the immigrated `selection` needn't be namespace-quolified
(selection 'clojure.set '[union intersection])


;; Existing vars (ns-map entries) are overwritten silently.

(fact "we start out with nothing"
  (contains? (ns-map *ns*) 'root-value) => false
  (contains? (ns-map *ns*) 'has-root-value?) => false)

(def root-value "this value will be overwritten by the version from such.vars.")
;; ... as will this referred value
(require '[such.vars :refer [has-root-value?]])

(fact "The above two lines produce this situation:"
  (contains? (ns-map *ns*) 'root-value) => true
  (contains? (ns-interns *ns*) 'root-value) => true
  (contains? (ns-refers *ns*) 'root-value) => false
  root-value => string?

  (contains? (ns-map *ns*) 'has-root-value?) => true
  (contains? (ns-interns *ns*) 'has-root-value?) => false
  (contains? (ns-refers *ns*) 'has-root-value?) => true)

;; Now we immigrate "over" the previous versions.
(immigrate/namespaces 'such.vars)

(fact "the interned value is interned - but with a different value"
  (contains? (ns-map *ns*) 'root-value) => true
  (contains? (ns-interns *ns*) 'root-value) => true
  (contains? (ns-refers *ns*) 'root-value) => false
  (root-value 'root-value) =not=> string?)

(fact "the referred value is now interned"
  (contains? (ns-map *ns*) 'has-root-value?) => true
  (contains? (ns-interns *ns*) 'has-root-value?) => true
  (contains? (ns-refers *ns*) 'has-root-value?) => false)
