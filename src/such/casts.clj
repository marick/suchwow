(ns such.casts
  "\"Be conservative in what you send, be liberal in what you accept.\"
         -- Postel's Robustness Principle
  
   Some Clojure functions require specific types of arguments, such as
   a symbol representing a namespace. You can use these functions to
   convert from what you've got to what Clojure requires. Or you can
   use them to build more accepting variants of those Clojure
   functions."
  (:use such.types)
  (:require [such.vars :as var]
            [such.util.fail :as fail]
            [clojure.string :as str]))


(defn- no-namespace [sym] (symbol (name sym)))

;; Util
(defn as-ns-symbol
  "The argument *must* be a symbol, namespace, or string. In all cases, 
   the result is a symbol with no namespace:

       (as-ns-symbol *ns*) => 'such.casts    
       (as-ns-symbol \"clojure.core\") => 'clojure.core    
       (as-ns-symbol 'clojure.core) => 'clojure.core    
       (as-ns-symbol 'clojure.core/food.dinner) => 'food.dinner

   Use with namespace functions that require a symbol ([[find-ns]], etc.) or 
   to convert the result of functions that return the wrong sort of reference
   to a namespace. (For example,`(namespace 'a/a)` returns a string.)"
  [arg]
  (cond (namespace? arg) (ns-name arg)
        (symbol? arg) (no-namespace arg)
        (string? arg) (symbol arg)
        :else (fail/bad-arg-type 'as-ns-symbol arg)))

(defn as-var-name-symbol
  "The argument *must* be a symbol, string, or var. In all cases, the 
   result is a symbol without a namespace:

       (as-var-name-symbol 'clojure.core/even?) => 'even?
       (as-var-name-symbol #'clojure.core/even?) => 'even?
       (as-var-name-symbol \"even?\") => 'even?

   Use with namespace functions that require a symbol ([[ns-resolve]], etc.)"
  [arg]
  (cond (var? arg) (var/name-as-symbol arg)
        (symbol? arg) (no-namespace arg)
        (string? arg) (symbol arg)
        :else (fail/bad-arg-type 'as-var-name-symbol arg)))
  
(defn as-name-string
  "The argument *must* be a symbol, string, keyword, or var. The result is a 
   string, usually used as part of a symbol's name.

        (as-name-string 'clojure/foo) => \"foo\"   ; namespace omitted
        (as-name-string #'even?) => \"even?\"
        (as-name-string :bar) => \"bar\"           ; colon omitted.
        (as-name-string :util.x/quux) => \"quux\"  ; \"namespace\" omitted
"
  [arg]
  (cond (var? arg) (var/name-as-string arg)
        (symbol? arg) (name arg)
        (string? arg) arg
        (keyword? arg) (name arg)
        :else (fail/bad-arg-type 'as-name-string arg)))
        
(defn as-namespace-and-name
  "`val` is something that can be thought of as having namespace and name parts.
   This function splits `val` and returns those two parts as symbols, except that
   the namespace may be nil. Accepts symbols, keywords, vars, and strings containing
   at most one slash.

       (as-two-part-name 'clojure.core/even?) => ['clojure.core 'even?]
       (as-two-part-name :foo) => [nil 'foo]

       (as-two-part-name #'even) => ['clojure.core 'even?]

       (as-two-part-name \"even?\") => [nil 'even?]
       (as-two-part-name \"clojure.core/even?\") => ['clojure.core 'even?]
       (as-two-part-name \"foo/bar/baz\") => (throws)
"
  [val]
  (letfn [(pairify [substrings]
                   (case (count substrings)
                     1 (vector nil (symbol (first substrings)))
                     2 (into [] (map symbol substrings))
                     (fail/not-namespace-and-name)))]
    (cond (string? val)
          (pairify (str/split val #"/"))

          (instance? clojure.lang.Named val)
          (pairify (remove nil? ((juxt namespace name) val)))

          (var? val)
          (vector (ns-name (.ns val)) (.sym val))

          :else
          (fail/not-namespace-and-name))))
