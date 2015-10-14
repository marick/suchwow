(ns such.maps
  "Various functions on key-value structures"
  (:require [such.versions :refer [when=1-6]]))

(when=1-6
 (defn update
   "The update function from Clojure 1.7. The same as `update-in` except
    the second argument is a key instead of a path.
    
         (subject/update {:a 1} + 5)"
   [m k f & args]
   (apply update-in m [k] f args))
 )

(defn invert
  "Produce a map with values as keys.
   Values are assumed unique."
  [map]
  (reduce (fn [so-far [key val]]
            (assoc so-far val key))
          {}
          map))

(defn conj-into
  "`original` is a map. `additions` is a sequence of keys and values (not a map).
  Each key is used to identify a value within the map. That `original` value is
  updated by conjing on the associated `additions` value.
   
        (conj-into {:a [1] :b '(1)} :a 2 :b 2) => '{:a [1 2] :b (2 1)}
   
   If the key isn't present in the map, it is created as a list containing
   the value.

        (conj-into {} :a 1) => '{:a (1)}
"
  [original & additions]
  (loop [[k v & more :as all] additions
         so-far original]
    (if (empty? all) 
      so-far
      (recur more
             (update-in so-far [k] conj v)))))

(defn dissoc-keypath
  "Like `dissoc`, but takes a sequence of keys that describes a path to a value.
   There must be at least two keys in the path.
    
         (subject/dissoc-keypath {:by-name {:name1 1}} [:by-name :name1])
          =>                     {:by-name {        }}
"
  [map keys]
  (let [[path-to-end-key end-key] [(butlast keys) (last keys)]
        ending-container (get-in map path-to-end-key)
        without-key (dissoc ending-container end-key)]
    (assoc-in map path-to-end-key without-key)))



(defn key-difference
  "Remove (as with `dissoc`) all the keys in `original` that are in
   `unwanted`.
   
        (key-difference {:a 1, :b 2} {:b ..irrelevant.., :c ..irrelevant..}) => {:a 1}
"

  [original unwanted]
  (apply dissoc original (keys unwanted)))

(defn update-each-value
  "Call `f` on each value in map `kvs`, passing it the value and
   any `args`.
    
        (update-each-value {:a 1, :b 0} + 2) => {:a 3, :b 2}
"
  [kvs f & args]
  (reduce (fn [so-far k] 
            (assoc so-far k (apply f (get kvs k) args)))
          kvs
          (keys kvs)))

(defn mkmap:all-keys-with-value
  "Create a map with keys `keys`. Each key will have `v` as
   its value.
       
          (mkmap:all-keys-with-value [:a, :b] 3) => {:a 3, :b 3}
"
  [keys v]
  (reduce (fn [so-far k]
            (assoc so-far k v))
          {}
          keys))
