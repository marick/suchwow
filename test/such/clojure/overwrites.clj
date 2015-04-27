(ns such.clojure.overwrites
  (:require [such.immigration :as immigrate]
            such.shorthand
            such.types
            [such.clojure.other-namespace :refer [namespace?]])
  (:use midje.sweet))

;; Existing interned vars will be overwritten silently

(def third 3)
(immigrate/namespaces 'such.shorthand)

(fact "change was made"
  (third [1 2 3]) => 3)

;; However, vars that "belong" to another namespace will produce an error upon immigration.
;; This is consistent with the behavior when you try to `def` or `intern` "over" such a var.

(fact
  (immigrate/selection 'such.types '[namespace?]) => (throws)
  (namespace?) => "I will survive! I'll stay alive!")


(fact "private variables are also not immigrated"
  #'such.clojure.other-namespace/not-immigrated =not=> bound?
  (find-var 'such.clojure.overwrites/not-immigrated) => nil)
