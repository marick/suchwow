(ns such.clojure.core
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
