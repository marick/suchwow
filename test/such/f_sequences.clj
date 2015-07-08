(ns such.f-sequences
  (:require [such.sequences :as subject])
  (:use midje.sweet))

(fact "vertical-slices"
  (subject/vertical-slices [1 2 3] [:a :b :c])
  => [ [1 :a] [2 :b] [3 :c]])

(fact "only"
  (subject/only [1]) => 1
  (subject/only [1 2]) => (throws #"`\[1 2\]` should have only one")
  (subject/only []) => (throws #"`\[\]` should have only one"))


(fact +into
  (subject/+into [1]) => [1]
  (subject/+into [1] [2 3]) => [1 2 3]
  (subject/+into [1] [2 3] [4 5] [] [6]) => [1 2 3 4 5 6])

(fact bifurcate
  (subject/bifurcate even? [1 2 3 4]) => [ [2 4] [1 3] ]
  (take 5 (first (subject/bifurcate even? (range)))) => [0 2 4 6 8])
