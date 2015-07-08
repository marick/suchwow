(ns such.sequences
  (require [such.wrongness :as !]))

(defn vertical-slices
  "Given N sequences, return one sequence whose first element
   is a sequence of all the first elements, etc."
  [& sequences]
  (apply (partial map (fn [& args] args)) sequences))


(defn only
  "Gives the sole element of a sequence"
  [coll]
  (if (seq (rest coll))
    (!/boom! "`%s` should have only one element." coll)
    (if (seq coll)
      (first coll)
      (!/boom! "`%s` should have only one element." coll))))

