(ns such.f-immigration (:require [such.immigration :as immigrate])
  (:use midje.sweet))

;;; This creates a "favorite functions" namespace. The imported functions
;;; are available here. See `f_use_favorites.clj` to see that the
;;; imported functions can be "referred into" another namespace.

;; Potemkin's original import function
(immigrate/import-vars [clojure.math.combinatorics
                         count-permutations permutations]
                       [clojure.data.json
                         write-str])

(fact
  (count-permutations [1 2 3]) => 6
  (permutations [1 2]) => [ [1 2] [2 1] ]

  (write-str [1 2]) => "[1,2]")


;;; Importing everything
(immigrate/import-all-vars clojure.set)

(fact
  (subset? #{1} #{1 2}) => true)


;;; Importing everything but adding a prefix
(immigrate/import-prefixed-vars clojure.string str-)

(fact 
  (str-trim "   f     ") => "f")
