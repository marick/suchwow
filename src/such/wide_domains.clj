(ns such.wide-domains
  "Variants of clojure.core functions that accept more types of inputs."
  (:refer-clojure :exclude [find-var symbol])
  (:require [such.casts :as cast]
            [clojure.string :as str]
            [such.util.fail :as fail]))


(defn symbol
  "Creates a symbol.    
  The `ns` argument may be a namespace, symbol, keyword, or string ([[as-ns-string]]).    
  The `name` argument may be a symbol, string, keyword, or var ([[as-string-without-namespace]]).

  In the one-argument version, the resulting symbol has a `nil` namespace.
  In the two-argument version, it has the symbol version of `ns` as the namespace.
  Note that `ns` need not refer to an existing namespace.

      (symbol \"th\") => 'th
      (symbol 'clojure.core \"th\") => 'clojure.core/th

      (symbol *ns* 'th) => 'this.namespace/th ; \"add\" a namespace
      (symbol *ns* 'clojure.core/even?) => 'this.namespace/even? ; \"localize\" a symbol.
"
([name]
 (clojure.core/symbol (cast/as-string-without-namespace name)))
([ns name]
  (clojure.core/symbol (str (cast/as-ns-symbol ns)) (cast/as-string-without-namespace name))))

(defn find-var 
  "Return a variable identified by the arguments, or `nil`.

   *Case 1*:
   If the single argument is a namespace-qualified symbol, the behavior is
   the same as `clojure.core/find-var`: the variable of that name in that
   namespace is returned:
   
       (find-var 'clojure.core/even?) => #'clojure.core/even?
   
   Note that the namespace *must* exist or an exception is thrown. 

   Strings with a single slash are treated as symbols:

       (find-var \"clojure.core/even?\") => #'clojure.core/even?

   Namespace-qualified keywords can also be used.

   *Case 2*:
   If the single argument is not namespace-qualified, it is treated as if it 
   were qualified with `*ns*`:

       (find-var 'find-var) => #'this.namespace/find-var
       (find-var \"symbol\") => #'this.namespace/symbol

   *Case 3*:
   If the single argument is a var, it is returned.

   *Case 4*:
   In the two-argument case, the `ns` argument supplies the namespace and 
   the `name` argument the var's name. `ns` may be a namespace, symbol, keyword,
   or string ([[as-ns-symbol]]). `name` may be a string, symbol, keyword,
   or var. In the first three cases, the namespace part of `name` (if any)
   is ignored:
   
       (find-var 'such.wide-domains 'clojure.core/find-var) => #'such.wide-domains/find-var
       (find-var *ns* :find-var) => #'this.namespace/find-var

   If the `name` argument is a var, `find-var` looks for a var with the same name
   in `ns`:
   
       (find-var 'such.wide-domains #'clojure.core/find-var) => #'such.wide-domains/find-var
"
  ([name]
     (if (var? name)
       name
       (let [[ns name] (cast/as-namespace-and-name-symbols name)]
         (clojure.core/find-var (symbol (or ns *ns*) name)))))
  ([ns name]
     (let [ns-sym (cast/as-ns-symbol ns)
           name-sym (cast/as-symbol-without-namespace name)]
       (clojure.core/find-var (symbol ns-sym name-sym)))))
