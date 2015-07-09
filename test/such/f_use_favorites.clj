(ns such.f-use-favorites 
  (:use such.f-immigration)
  (:use midje.sweet))

(fact
  (count-permutations [1 2 3]) => 6
  (permutations [1 2]) => [ [1 2] [2 1] ]

  (write-str [1 2]) => "[1,2]")

(fact
  (subset? #{1} #{1 2}) => true)

(fact 
  (str-trim "   f     ") => "f")
