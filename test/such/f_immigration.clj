(ns such.f-immigration (:require [such.immigration :as immigrate])
  (:use midje.sweet))

(immigrate/import-all-vars clojure.set)

(fact
  (subset? #{1} #{1 2}) => true)

(immigrate/import-prefixed-vars clojure.string str-)

(fact 
  (str-trim "   f     ") => "f")
