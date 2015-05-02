(ns such.clojure.core
  "This namespace demonstrates the creation of a 'catch-all' namespace that contains
   the vars from several other namespaces *and* can make those vars available to
   other namespaces that `:require` this one."
  (:require [such.immigration :as immigrate])
  (:use midje.sweet))

;; Here are the three ways to immigrate:
;; 1. Every public var in a set of namespaces
(immigrate/namespaces 'such.shorthand 'such.types)
;; 2. Selected vars in a single namespace
(immigrate/selection 'such.immigration '[move-var! selection])
;; 3. Every public var in a namespace, with the resulting vars in this namespace prefixed.
(immigrate/prefixed 'clojure.string "str-")

;; Go to the f_core.clj file in this directory to see use of this namespace.

;;; Note: this example does not include such.better-doc or such.wide-domains. When
;;; those are immigrated into here, `:use` or `:refer :all` of this namespace from
;;; another one will produce these warnings:
;;; 
;;; WARNING: find-ns already refers to: #'clojure.core/find-ns in namespace: user, being replaced by: #'commons.clojure.core/find-ns
;;; 
;;; See such.clojure.more-core for a way to handle that.



;; Other possibly interesting facts

(fact "you can immigrate a private var, but it remains private (see f-core for confirmation)"
  (meta #'move-var!) => (contains {:private true}))

;; It is safe to immigrate twice.
(selection 'clojure.set '[union]) ; Because `selection` is immigrated, it needn't be namespace-quolified
(selection 'clojure.set '[union intersection])


