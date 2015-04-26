(ns such.symbols
  "Symbol utilities, such as different ways to create symbols."
  (:require [such.casts :as cast]
            [clojure.string :as str]))

(defn from-concatenation 
  "Construct a symbol from the concatenation of the string versions of the
   `nameables`, which may be symbols, strings, keywords, or vars. If given,
   the `join-nameable` is interposed between the segments.
   
        (symbol/from-concatenation ['a \"b\" :c #'d]) => 'abcd
        (symbol/from-concatenation [\"a\" \"b\"] '-) => 'a-b)
   
   Note that the namespace qualifiers for symbols and strings are not included:
   
        (symbol/from-concatenation [:namespace/un #'clojure.core/even?]) => 'uneven?
"
  ([nameables join-nameable]
     (symbol (str/join (cast/as-name-string join-nameable) (map cast/as-name-string nameables))))
  ([nameables]
     (from-concatenation nameables "")))

(defn without-namespace
  "Return a symbol with the same name as `sym` but no 
   namespace.
   
        (symbol/without-namespace 'clojure.core/even?) => 'even?
"
  [sym]
  (symbol (name sym)))
