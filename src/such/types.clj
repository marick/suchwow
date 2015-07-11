(ns such.types
  "Type (and \"type-like\") predicates not included in clojure.core."
  (:use such.versions))

(defn regex?
  "Is x a regular expression (a Java Pattern)?"
  [x]
  (instance? java.util.regex.Pattern x))

(defn stringlike?
  "Is x a string or a regex?"
  [x]
  (or (string? x) (regex? x)))

(defn classic-map?
  "`map?` will return true for Records. This returns true only for hashmaps and sorted maps."
  [x]
  (instance? clojure.lang.APersistentMap x))

(when<=1-5
  (defn record?
    "Is x a record?"
    [x]
    (instance? clojure.lang.IRecord x)))

(defn big-decimal?
  "Is x a Java BigDecimal?"
  [x]
  (instance? java.math.BigDecimal x))

(defn multi? 
  "Was `x` created with `defmulti`?"
  [x]
  (instance? clojure.lang.MultiFn x))

(defn extended-fn?
  "`fn?` does not consider multimethods to be functions. This does."
  [x]
  (or (fn? x) (multi? x)))
  
(defn named?
  "Will `name` work on x? Two cases: It implements the Named protocol OR it's a string."
  [x]
  (or (string? x)
      (instance? clojure.lang.Named x)))

(defn linear-access?
  "Is the collection one where you can't do better than linear access?"
  [x]
  (or (list? x)
      (seq? x)))

(defn namespace?
  "Is x a namespace?"
  [x]
  (instance? clojure.lang.Namespace x))
