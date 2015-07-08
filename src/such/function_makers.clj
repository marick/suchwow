(ns such.function-makers
  "Functions that make other functions.
   
   Commonly used with a naming convention that flags such functions with
   `mkfn`:
   
       (ns ...
         (:require [such.function-makers :as mkfn]))
       ...
       (def stringlike? (mkfn/any-pred string? regex?))
")


(defn any-pred
  "Constructs a strict predicate that takes a single argument.
   That predicate returns `true` iff any of the `preds` is 
   truthy of that argument.
   
        (def stringlike? (mkfn:any-pred string? regex?))
        (stringlike? []) => false
        (stringlike? \"\") => true
   
        (def has-favs? (mkfn/any-pred (partial some #{0 4}) odd?)
        (has-favs? [2 4]) => true
        (has-favs? [1 6]) => true
   
   Stops checking after the first success. A predicate created from
   no arguments always returns `true`."
  [& preds]
  (if (empty? preds)
    (constantly true)
    (fn [arg]
      (loop [[candidate & remainder :as preds] preds]
        (cond (empty? preds)  false
              (candidate arg) true
              :else           (recur remainder))))))
