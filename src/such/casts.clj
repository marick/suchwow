(ns such.casts
  "Some Clojure functions require specific types of arguments, such as a symbol
   representing a namespace. But, for many purposes, you'd just as soon that
   function work with the namespace itself. You can use these functions to 
   convert from what you've got to what Clojure wants. Or you can use them to
   build more accepting variants of those Clojure functions."
  (:use such.types))

(defn- badtype [name]
  (throw (new Exception (str "Bad argument type for " name))))

(defn- var-name [var] (:name (meta var)))
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
   with functions that return some sort of reference to a namespace. (For example, 
   `(namespace 'a/a)` returns a string.)"
  [arg]
  (cond (namespace? arg) (ns-name arg)
        (symbol? arg) (no-namespace arg)
        (string? arg) (symbol arg)
        :else (badtype 'as-ns-symbol)))

(defn as-var-name-symbol
  "The argument *must* be a symbol, string, or var. In all cases, the 
   result is a symbol without a namespace:

       (as-var-name-symbol 'clojure.core/even?) => 'even?
       (as-var-name-symbol #'clojure.core/even?) => 'even?
       (as-var-name-symbol \"even?\") => 'even?

   Use with namespace functions that require a symbol ([[ns-resolve]], etc.)"
  [arg]
  (cond (var? arg) (var-name arg)
        (symbol? arg) (no-namespace arg)
        (string? arg) (symbol arg)
        :else (badtype 'as-var-name-symbol)))
  

