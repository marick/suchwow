(ns such.better-doc
  (:refer-clojure :exclude [find-ns ns-name ns-resolve resolve]))

;;; I'd be ecstatic if documentation like this, or derived from this, were
;;; included in clojure.core. Note that the Unlicense allows anyone to do that.

(def ns-name 
  "`ns` must be either a namespace or a symbol naming a namespace.
   If the namespace exists, its symbol-name is returned. If not, an exception is thrown.
   Note: the more common `name` cannot be applied to namespaces.
   Example:
      (ns-name *ns*) => 'such.better-doc'"
  clojure.core/ns-name)

(def find-ns
  "`sym` must be a symbol. If it names an existing namespace, that namespace is returned.
   Otherwise, `nil` is returned.
   Example:
     (find-ns 'clojure.core) => #<Namespace clojure.core>"
  clojure.core/find-ns)

(def ns-resolve 
  "`ns-resolve` goes from a symbol to the var or class it represents.

   The first (`ns`) argument must be either a namespace or a symbol naming a namespace 
   (e.g., `clojure.core`). The final argument `sym` must be a symbol.
   There are four cases for that final argument:

   1. `sym` is not namespace qualified (e.g., `'even?`), and you hope it corresponds 
      to a var in `ns`. If there is a var that (1) is \"available\" in `ns` and 
      (2) has the same name as `sym`, it is returned. Otherwise, `nil` is returned.
      \"Available\" means it has either been `intern`ed in the namespace or `refer`ed
      into it. 
          (ns-resolve *ns* 'a-var) => #'this.namespace/a-var
          (ns-resolve *ns* 'even?) => #'clojure.core/even?
          (ns-resolve 'clojure.core 'even?) => #'clojure.core/even?

   2. `sym` is a namespace-qualified symbol (e.g., `'clojure.core/even?`) that you
       hope corresponds to a var. Prefer `resolve` to this case. Nevertheless:
       The behavior is the same as (1), except that `ns` is not used. (It must
       still either be or refer to a namespace, though, else an exception will be
       thrown.) The symbol's namespace is used instead. 
          (ns-resolve *ns* 'clojure.core/even?) => #'clojure.core/even?
       
   3. `sym` is a fully qualified class name (e.g., `'java.lang.Object). If such
      a class exists, it is returned. Otherwise, a ClassNotFoundException is thrown. 
      The `ns` argument is ignored, except that it must be a namespace or a symbol 
      naming one. For that reason, prefer `resolve` in this case.
          (ns-resolve *ns* 'java.lang.Object) => java.lang.Object

   4. `sym` is a symbol you hope names a class imported into `ns`. If there is
      a class with that (unqualified) name in `ns`, it is returned.
          (ns-resolve 'clojure.core 'Object) => java.lang.Object
          (import 'java.util.AbstractCollection)
          (ns-resolve *ns* 'AbstractCollection) => java.util.AbstractCollection
      If the class hasn't been imported, `nil` is returned. Note that this is 
      a difference from the qualified case:
          (ns-resolve 'clojure.core 'AbstractMethodHandlerFactoryFactory) => nil
          (ns-resolve 'clojure.core 'java.lang.AbstractMethodHandlerFactoryFactory) => (throws)

   In the three-argument case, the second `env` argument is a map whose keys 
   must be symbols. If any of the keys are `=` to the final argument, `nil` is returned.
          (ns-resolve *ns* '{even? \"irrelevant\"} 'even?) => nil
          (ns-resolve *ns* '{Object \"irrelevant\"} 'Object) => nil
   "
  clojure.core/ns-resolve)
