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

(defn- namespacishly-split [s]
  (str/split s #"/"))

(defn- string-parts
  [s]
  (let [substrings (namespacishly-split s)]
    (case (count substrings)
      1 (vector nil (symbol (first substrings)))
      2 (into [] (map symbol substrings))
      (fail/not-namespace-and-name))))

(defn- named-namespace [named]
  (if (string? named)
    (first (string-parts named))
    (namespace named)))

(defn- named-name [named]
  (if (string? named)
    (second (string-parts named))
    (name named)))

(defn has-namespace?
  "`arg` *must* satisfy [[named?]] (string or `clojure.lang.Named`).
  Returns true iff the `arg` has a non-`nil`namespace. For a string, 
  \"has a namespace\" means it contains exactly one slash - the part
  before the slash is considered the namespace.
   
      (has-namespace? :foo) => false
      (has-namespace? 'clojure.core/even?) => true
      (has-namespace? \"clojure.core/even?\") => true
"
  [arg]
  (when-not (named? arg) (fail/not-namespace-and-name arg))
  (boolean (named-namespace arg)))

(defn as-ns-symbol
   "Returns a symbol with no namespace.
   Use with namespace functions that require a symbol ([[find-ns]], etc.) or 
   to convert the result of functions that return the wrong sort of reference
   to a namespace. (For example,`(namespace 'a/a)` returns a string.)

   The argument *must* be a namespace, symbol, keyword, or string.
   In the latter three cases, `arg` *must not* have a namespace. 
   (But see [[with-namespace-as-symbol]].) 
   (Note: strings have \"namespaces\" if they contain exactly one slash.
   See [[as-namespace-and-name-symbols]].)

   The result is a symbol with no namespace. There are two cases:
   1. If `arg` is a namespace, its symbol name is returned:
      
          (as-ns-symbol *ns*) => 'such.casts    

   2. Otherwise, the \"name\" of the `arg` is converted to a symbol:
      
          (as-ns-symbol \"clojure.core\") => 'clojure.core
          (as-ns-symbol 'clojure.core) => 'clojure.core
          (as-ns-symbol :clojure.core) => 'clojure.core
   "

  [arg]
  (cond (namespace? arg)
        (ns-name arg)

        (has-namespace? arg)
        (fail/should-not-have-namespace 'as-ns-symbol arg)

        :else
        (symbol (named-name arg))))

(defn with-namespace-as-symbol
   "Extract the namespace from `arg`.

   The argument *must* be a namespace, symbol, keyword, or string.
   In the latter three cases, `arg` *must* have a namespace. 
   (Note: strings have \"namespaces\" if they contain exactly one slash.

   The result is a symbol with no namespace. There are two cases:
   1. If `arg` is a namespace, its symbol name is returned:
      
          (with-namespace-as-symbol *ns*) => 'such.casts

   2. Otherwise, the \"namespace\" of the `arg` is converted to a symbol:
           
          (with-namespace-as-symbol \"clojure.core/even?\") => 'clojure.core
          (with-namespace-as-symbol 'clojure.core/even?) => 'clojure.core
          (with-namespace-as-symbol :clojure.core/even?) => 'clojure.core
   "
   [arg]
   (cond (namespace? arg)
         (ns-name arg)
         
         (not (has-namespace? arg))
         (fail/should-have-namespace 'with-namespace-as-symbol arg)
         
         :else
         (symbol (named-namespace arg))))


(defn as-namespace-and-name-symbols
  "`val` is something that can be thought of as having namespace and name parts.
   This function splits `val` and returns those two parts as symbols, except that
   the namespace may be nil. Accepts symbols, keywords, vars, and strings containing
   at most one slash.

       (as-namespace-and-name-symbols 'clojure.core/even?) => ['clojure.core 'even?]
       (as-namespace-and-name-symbols :foo) => [nil 'foo]

       (as-namespace-and-name-symbols #'even) => ['clojure.core 'even?]

       (as-namespace-and-name-symbols \"even?\") => [nil 'even?]
       (as-namespace-and-name-symbols \"clojure.core/even?\") => ['clojure.core 'even?]
       (as-namespace-and-name-symbols \"foo/bar/baz\") => (throws)
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

(defn as-symbol-without-namespace
  "The argument *must* be a symbol, string, keyword, or var. In all cases, the 
   result is a symbol without a namespace:

       (as-symbol-without-namespace 'clojure.core/even?) => 'even?
       (as-symbol-without-namespace #'clojure.core/even?) => 'even?
       (as-symbol-without-namespace :clojure.core/even?) => 'even?
       (as-symbol-without-namespace :even?) => 'even?
       (as-symbol-without-namespace \"even?\") => 'even?
       (as-symbol-without-namespace \"core.foo/bar\") => 'bar

   Use with namespace functions that require a symbol ([[ns-resolve]], etc.)"
  [arg]
  (second (as-namespace-and-name-symbols arg)))
  
(defn as-string-without-namespace [arg]
  "The argument *must* be a symbol, string, keyword, or var. The result is a 
   string name that omits the namespace:

        (as-string-without-namespace 'clojure/foo) => \"foo\"      ; namespace omitted
        (as-string-without-namespace #'even?) => \"even?\"
        (as-string-without-namespace :bar) => \"bar\"              ; colon omitted.
        (as-string-without-namespace :util.x/quux) => \"quux\"     ; \"namespace\" omitted
        (as-string-without-namespace \"util.x/quux\") => \"quux\"  ; \"namespace\" omitted
"
  [arg]
  (str (as-symbol-without-namespace arg)))
        
