(ns such.f-use-types
  "I expect types to be `:use`d (or, if you like a less readable way of doing the same thing, 
  `:refer :all`)."
  (:use such.versions such.types)
  (:use midje.sweet))

(defrecord R [a])

(facts "so, for example..."
  (stringlike? "s") => true)


(facts "don't override 1.6 version of `record?` (which would produce a warning message)"
  (record? (R. 1)) => true
  (record? (hash-map)) => false
  (record? (sorted-map)) => false
  (record? 1) => false)
