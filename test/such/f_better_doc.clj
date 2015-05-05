(ns such.f-better-doc (:require such.better-doc)
  (:use midje.sweet such.types))


(fact "doc strings have been updated"
  (resolve 'find-ns) => #'clojure.core/find-ns
  (-> #'find-ns meta :doc) => #"Other examples")

;;; The following are just tests derived from the documentation, demonstrating 
;;; that it was true at one time.

(def a-var)

;; ns-name
(fact "`ns-name` converts either a namespace or its symbol representation into a symbol"
  (ns-name 'such.f-better-doc) => 'such.f-better-doc
  (ns-name (find-ns 'such.f-better-doc)) => 'such.f-better-doc
  (ns-name 'no.such.namespace) => (throws #"No namespace.*found")

  (fact "Note that `name` doesn't work with namespaces"
    (name *ns*) => (throws)))

;; find-ns
(fact "`find-ns` only works on a symbol, not a namespace"
      (find-ns 'such.f-better-doc) => namespace?
      (find-ns (find-ns 'such.f-better-doc)) => (throws))

;; ns-resolve
(fact "ns-resolve can be used to find vars"
  (fact "it's straightforward for a non-qualified symbol"
    (ns-resolve *ns* 'a-var) => #'a-var
    (ns-resolve *ns* 'i-no-exist) => nil)
  
  (fact "the namespace argument is ignored when the symbol is namespace-qualified"
    (ns-resolve *ns* 'clojure.core/i-no-exist) => nil
    (ns-resolve *ns* 'clojure.core/even?) => #'even?
    (ns-resolve (find-ns 'clojure.core) 'clojure.core/even?) => #'even?)

  (fact "the first argument may also be a symbol"
    (ns-resolve 'clojure.core 'even?) => #'even?
    (ns-resolve 'clojure.core 'i-no-exist) => nil)

  (fact "if the symbol argument does not name an existing namespace, an exception is thrown"
    (ns-resolve 'gorp.foo 'even?) => (throws #"No namespace")))

(fact "`ns-resolve` can also be used to find classes"
  (fact "with fully-qualified names"
    (ns-resolve *ns* 'java.util.AbstractCollection) => java.util.AbstractCollection
    (ns-resolve 'clojure.core 'java.util.AbstractCollection) => java.util.AbstractCollection

    (fact "the namespace is irrelevant except that it should exist"
      (ns-resolve 'derp.foo 'java.util.AbstractCollection) => (throws #"No namespace")))

  (fact "it can also be used to find imported names"
    (ns-resolve *ns* 'Object) => java.lang.Object
    (import 'java.util.AbstractCollection)
    (ns-resolve *ns* 'AbstractCollection) => java.util.AbstractCollection
    (fact "in such a case, the namespace matters"
      ;; Note that the result is nil, as with var lookup, not as with fully-qualified class lookup
      (ns-resolve 'clojure.core 'AbstractCollection) => nil)))

(fact "`ns-resolve takes an `env` argument that can force it to return nil instead of a var"
  (ns-resolve *ns* {} 'a-var) => #'a-var
  (ns-resolve *ns* {'a-var ..irrelevant..} 'a-var) => nil
  (ns-resolve 'clojure.core {'even? ..irrelevant..} 'even?) => nil

  (fact "a namespace-qualified symbol is not masked by a symbol with the same name"
    (ns-resolve 'clojure.core {'even? ..irrelevant..} 'clojure.core/even?) => #'even?
    (ns-resolve *ns* {'a-var ..irrelevant..} 'such.f-better-doc/a-var) => #'a-var

    (fact "to mask it, the \"env\" must contain the namespace-qualified name (which
           I imagine never happens"
      ;; Note that the namespace is still irrelevant
      (ns-resolve *ns* {'clojure.core/even? ..irrelevant..} 'clojure.core/even?) => nil
      (ns-resolve 'clojure.core {'such.f-better-doc/a-var ..irrelevant..} 'such.f-better-doc/a-var) => nil)))


(fact "The `env` argument can also prevent lookup of classes"
  (ns-resolve *ns* {} 'Object) => java.lang.Object
  (ns-resolve *ns* {'Object ..irrelevant..} 'Object) => nil

  (fact "as with vars, lookup of a fully-qualified classname-symbol requires same of map argument."
  (ns-resolve *ns* {} 'java.lang.Object) => java.lang.Object
  (ns-resolve *ns* {'Object ..irrelevant..} 'java.lang.Object) => java.lang.Object
  (ns-resolve *ns* {'java.lang.Object ..irrelevant..} 'java.lang.Object) => nil))



(fact symbol
  (symbol "th") => 'th
  (symbol *ns* "th") => (throws)
  (symbol 'such.f-better-doc "th") => (throws)
  (symbol "such.f-better-doc" "th") => 'such.f-better-doc/th
  (symbol "no.such.namespace" "th") => 'no.such.namespace/th)
