(ns such.clojure.core
  "This namespace demonstrates the creation of a 'catch-all' namespace that contains
   the vars from several other namespaces *and* can make those vars available to
   other namespaces that `:require` this one."
  (:require [such.immigration :as immigrate]
            ;; the following updates docstrings for clojure.core
            such.better-doc)
  (:use midje.sweet))

;; Here are the three ways to immigrate:
;; 1. Every public var in a set of namespaces
(immigrate/namespaces 'such.shorthand 'such.types 'such.wide-domains)
;; 2. Selected vars in a single namespace
(immigrate/selection 'such.immigration '[move-var! selection])
;; 3. Every public var in a namespace, with the resulting vars in this namespace prefixed.
(immigrate/prefixed 'clojure.string "str-")

;; Go to the core-client.clj file in this directory to see use of this namespace.



;; Other possibly interesting facts

(fact "you can immigrate a private var, but it remains private"
  (meta #'move-var!) => (contains {:private true}))

;; It is safe to immigrate twice.
(selection 'clojure.set '[union]) ; Because `selection` is immigrated, it needn't be namespace-quolified
(selection 'clojure.set '[union intersection])

