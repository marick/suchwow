(ns such.better-doc
  "`clojure.core` functions, except with more complete documentation."
  (:require [such.vars :as vars]))

;;; I'd be ecstatic if documentation like this, or derived from this, were
;;; included in clojure.core. Note that the Unlicense allows anyone to do that.



(defmacro ^:private local-copy [var doc-string]
  `(let [var-name# (:name (meta ~var))]
     (ns-unmap *ns* var-name#)
     (intern *ns* (with-meta var-name#
                    (assoc (meta ~var) :doc ~doc-string)) (vars/root-value ~var))))

(local-copy #'clojure.core/ns-name
  "`ns` *must* be either a namespace or a symbol naming a namespace.
   If the namespace exists, its symbol-name is returned. If not, an exception is thrown.
   Note: the more common `name` function cannot be applied to namespaces.
   [Other examples](https://clojuredocs.org/clojure.core/ns-name)

       (ns-name *ns*) => 'such.better-doc
")

(local-copy #'clojure.core/find-ns
  "`sym` *must* be a symbol. If it names an existing namespace, that namespace is returned.
   Otherwise, `nil` is returned. [Other examples](https://clojuredocs.org/clojure.core/find-ns)

       (find-ns 'clojure.core) => #<Namespace clojure.core>
")

(local-copy #'clojure.core/ns-resolve
  "`ns-resolve` goes from a symbol to the var or class it represents. [Other examples](https://clojuredocs.org/clojure.core/ns-resolve)


   The first (`ns`) argument *must* be either a namespace or a symbol naming a namespace 
   (e.g., `'clojure.core`). The final argument `sym` *must* be a symbol.
   There are four cases for that final argument:

   1. `sym` is not namespace qualified (e.g., `'even?`), and you hope it corresponds 
      to a var in `ns`. If there is a var that (1) is \"available\" in `ns` and 
      (2) has the same name as `sym`, it is returned. Otherwise, `nil` is returned.
      \"Available\" means the var has either been `intern`ed in the namespace or `refer`ed
      into it.
      
           (ns-resolve *ns* 'a-var) => #'this.namespace/a-var
           (ns-resolve *ns* 'even?) => #'clojure.core/even?
           (ns-resolve 'clojure.core 'even?) => #'clojure.core/even?

   2.  `sym` is a namespace-qualified symbol (e.g., `'clojure.core/even?`) that you
       hope corresponds to a var. 
       The behavior is the same as (1), except that `ns` is not used.
       The symbol's namespace is used instead.     

           (ns-resolve *ns* 'clojure.core/even?) => #'clojure.core/even?

       Note: Even though the `ns` argument is not used in the lookup,
       it must still either be a namespace or a symbol that names an
       *existing* namespace. If not, an exception will be thrown.
      
       Because `ns` is unused, `resolve` is better for this case.
       
   3.  `sym` is a fully qualified class name (e.g., `'java.lang.Object`). If such
       a class exists, it is returned. Otherwise, a `ClassNotFoundException` is thrown. 
       The `ns` argument is ignored, except that it must be a namespace or a symbol 
       naming one.
      
           (ns-resolve *ns* 'java.lang.Object) => java.lang.Object

        Because `ns` is unused, `resolve` is better for this case.      

   4. `sym` is a symbol you hope names a class `import`ed into `ns`. If there is
       a class with that (unqualified) name in `ns`, it is returned.
       
           (ns-resolve 'clojure.core 'Object) => java.lang.Object
           (import 'java.util.AbstractCollection)
           (ns-resolve *ns* 'AbstractCollection) => java.util.AbstractCollection
       
       If the class hasn't been imported, the function returns `nil` (rather than 
       throwing an exception, as in the fully-qualified case).
       
           (ns-resolve 'clojure.core 'AbstractMethodHandlerFactoryFactory) => nil
           (ns-resolve 'clojure.core 'java.lang.AbstractMethodHandlerFactoryFactory) => (throws)

   In the three-argument case, the second `env` argument is a map whose keys 
   *must* be symbols. If any of the keys are `=` to the final argument, `nil` is
   returned (instead of a match, if any).

       (ns-resolve *ns* 'even?) => #'clojure.core/even?
       (ns-resolve *ns* '{even? \"irrelevant\"} 'even?) => nil
       (ns-resolve *ns* 'Object) => java.lang.Object
       (ns-resolve *ns* '{Object \"irrelevant\"} 'Object) => nil

   [Other examples](https://clojuredocs.org/clojure.core/ns-resolve)
")


(local-copy #'clojure.core/symbol
  "Creates a symbol from its arguments, which *must* be strings.
   The name of that result is `name`. In the one-argument version,
   the result's namespace is `nil`. In the two-argument version, it's
   `ns`. Note that `ns` need not refer to an existing namespace:
   
         (symbol \"no.such.namespace\" \"the\") => 'no.such.namespace/the
")
