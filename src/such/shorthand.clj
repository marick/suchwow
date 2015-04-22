(ns such.shorthand)

(defn any?
  "Return true if `pred` is true of any value in `coll`, false otherwise."
  [pred coll]
  (boolean (some pred coll)))

(defn not-empty? 
  "Return true if `value` has no values, false otherwise. `value` may be a collection,
     a String, a native Java array, or anything that implements the Iterable interface."
  [value]
  (boolean (seq value)))

(defn third 
  "Return the third element of `coll`. Returns nil if there aren't three elements."
  [coll]
  (nth coll 2))

(defn fourth
  "Return the fourth element of `coll`. Returns nil if there aren't four elements."
  [coll]
  (nth coll 3))
