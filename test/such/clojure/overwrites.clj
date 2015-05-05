(ns such.clojure.overwrites
  "Examples of importing symbols that are already referenced. (Silent overwriting.)
   See also such.clojure.overwrites-client."
  (:require [such.immigration :as immigrate]
            [such.clojure.other-namespace :refer [namespace?]])
  (:use midje.sweet))

;; Existing interned vars will be overwritten silently

(def third 3)
(immigrate/namespaces 'such.shorthand)

(fact "change was made"
  (third [1 2 3]) => 3)

;; Variables that "belong" to another namespace are also overwritten. 
;; This allows the creation of a supplementary "x.clojure.core" namespace
;; without a lot of annoying warnings.
(immigrate/selection 'such.types '[namespace?])

(fact
  ;; will get a "wrong arity" failure if version from other-namespace not overwritten
  (namespace? *ns*) => true)

;; Rather than interning the variables, you can `refer` it
(immigrate/namespaces-by-reference 'such.wide-domains)
(fact
  ((ns-refers *ns*) 'find-var) => #'such.wide-domains/find-var)



(fact "private variables are also not immigrated"
  #'such.clojure.other-namespace/not-immigrated =not=> bound?
  (find-var 'such.clojure.overwrites/not-immigrated) => nil)
