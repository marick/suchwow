(ns such.ns
  "Makes working with namespaces easier."
  (:use such.types)
  (:require [such.casts :as cast]
            [such.symbols :as sym]))

(defmacro with-scratch-namespace 
  "Create a scratch namespace named `ns-name`, run `body` within it, then 
   remove it. `ns-name` *must* be a symbol. If the namespace already
   exists, it will be removed, then recreated, then removed."
  [ns-sym & body]
  (when (and (sequential? ns-sym)
             (= (first ns-sym) 'quote))
    (println "You quoted the `ns-sym` arg to `with-scratch-namespace`. Don't do that."))
  `(try
     (remove-ns '~ns-sym)
     (create-ns '~ns-sym)
     ~@body
   (finally
     (remove-ns '~ns-sym))))

(defn +find-var
  "Return a variable identified by the arguments, or `nil`.
   A version of the built-in function, but with a wider domain.

   *Case 1*:
   If the single argument is a namespace-qualified symbol, the behavior is
   the same as `clojure.core/find-var`: the variable of that name in that
   namespace is returned:
   
       (+find-var 'clojure.core/even?) => #'clojure.core/even?
   
   Note that the namespace *must* exist or an exception is thrown. 

   Strings with a single slash are treated as symbols:

       (+find-var \"clojure.core/even?\") => #'clojure.core/even?

   Namespace-qualified keywords can also be used.

   *Case 2*:
   If the single argument is not namespace-qualified, it is treated as if it 
   were qualified with `*ns*`:

       (+find-var 'find-var) => #'this.namespace/find-var
       (+find-var \"symbol\") => #'this.namespace/symbol

   *Case 3*:
   If the single argument is a var, it is returned.

   *Case 4*:
   In the two-argument case, the `ns` argument supplies the namespace and 
   the `name` argument the var's name. `ns` may be a namespace, symbol, keyword,
   or string ([[as-ns-symbol]]). `name` may be a string, symbol, keyword,
   or var. In the first three cases, the namespace part of `name` (if any)
   is ignored:
   
       (+find-var 'such.wide-domains 'clojure.core/find-var) => #'such.wide-domains/find-var
       (+find-var *ns* :find-var) => #'this.namespace/find-var

   If the `name` argument is a var, `find-var` looks for a var with the same name
   in `ns`:
   
       (+find-var 'such.wide-domains #'clojure.core/find-var) => #'such.wide-domains/find-var
"
  ([name]
     (if (var? name)
       name
       (let [[ns name] (cast/as-namespace-and-name-symbols name)]
         (find-var (sym/+symbol (or ns *ns*) name)))))
  ([ns name]
     (let [ns-sym (cast/as-ns-symbol ns)
           name-sym (cast/as-symbol-without-namespace name)]
       (find-var (sym/+symbol ns-sym name-sym)))))
