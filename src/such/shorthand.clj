(ns such.shorthand
  "Explicit functions for what could be done easily - but less clearly - with clojure.core functions.
   Anti-shibboleths such as using `not-empty?` instead of `seq`.")


(def ^:no-doc this-var-has-no-value-and-is-used-in-testing)

(defn any?
  "`any?` provides shorthand for \"containment\" queries that otherwise
   require different functions. Behavior depends on the type of `predlike`.
   
   * A function: `true` iff `predlike` returns a *truthy* value for any value in `coll`.
     
            (any? even? [1 2 3]) => true           ; works best with boolean-valued functions
            (any? inc [1 2 3]) => true             ; a silly example to demo truthiness.
            (any? identity [nil false]) => false   ; also silly
     
   * A collection: `true` iff `predlike` contains any element of `coll`.

            (any? #{1 3} [5 4 1]) => true
            (any? [1 3] [5 4 1]) => true
        
        When `predlike` is a map, it checks key/value pairs:
        
            (any? {:a 1} {:a 1}) => true
            (any? {:a 1} {:a 2}) => false
            (any? {:a 2, :b 1} {:b 1, :c 3}) => true
     
   * A keyword: `true` iff `predlike` is a key in `coll`, which *must* be a map.
     
            (any? :a {:a 1, :b 2}) => true         ; equivalent to:
            (contains? {:a 1, :b 2} :a) => true
"
  [predlike coll]
  (boolean (cond (coll? predlike)
                 (some (set predlike) coll)

                 (keyword? predlike)
                 (contains? coll predlike)

                 :else
                 (some predlike coll))))

(defn not-empty? 
  "Returns `true` if `value` has any values, `true` otherwise. `value` *must* be a collection,
     a String, a native Java array, or something that implements the `Iterable` interface."
  [value]
  (boolean (seq value)))

(defn third 
  "Returns the third element of `coll`. Returns `nil` if there are fewer than three elements."
  [coll]
  (second (rest coll)))

(defn fourth
  "Returns the fourth element of `coll`. Returns `nil` if there are fewer than four elements."
  [coll]
  (third (rest coll)))

(defn find-first
  "Returns the first item of `coll` where `(pred item)` returns a truthy value, `nil` otherwise.
   `coll` is evaluated lazily. Note that an \"item\" in a map is a key-value pair:

        (find-first :key {:key \"value\"}) => nil
        (find-first #(= :key (first %)) {:key \"value\"}) => [:key \"value\"]
   "
  [pred coll]
  (first (filter pred coll)))

