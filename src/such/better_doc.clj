(ns such.better-doc
  "Requiring this file will replace some `clojure.core` docstrings with
   better versions.
   
   I'd be ecstatic if documentation like this, or derived from this, were
   included in clojure.core. Note that the Unlicense allows anyone to do that."
  (:require [such.vars :as vars]))


;; Interning copies in this namespace allows codox to find them.
(defmacro ^:private update-and-make-local-copy! [var doc-string]
  `(let [var-name# (:name (meta ~var))]
     (alter-meta! ~var assoc :doc ~doc-string)
     (ns-unmap *ns* var-name#)
     (intern *ns* (with-meta var-name#
                    (assoc (meta ~var) :doc ~doc-string)) (vars/root-value ~var))))

(update-and-make-local-copy! #'clojure.core/ns-name
  "`ns` *must* be either a namespace or a symbol naming a namespace.
   If the namespace exists, its symbol-name is returned. If not, an exception is thrown.
   Note: the more common `name` function cannot be applied to namespaces.
   [Other examples](https://clojuredocs.org/clojure.core/ns-name)

       (ns-name *ns*) => 'such.better-doc
")

(update-and-make-local-copy! #'clojure.core/find-ns
  "`sym` *must* be a symbol. If it names an existing namespace, that namespace is returned.
   Otherwise, `nil` is returned. [Other examples](https://clojuredocs.org/clojure.core/find-ns)

       (find-ns 'clojure.core) => #<Namespace clojure.core>
")

(update-and-make-local-copy! #'clojure.core/ns-resolve
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


(update-and-make-local-copy! #'clojure.core/symbol
  "Creates a symbol from its arguments, which *must* be strings.
   The name of that result is `name`. In the one-argument version,
   the result's namespace is `nil`. In the two-argument version, it's
   `ns`. Note that `ns` need not refer to an existing namespace:
   
         (symbol \"no.such.namespace\" \"the\") => 'no.such.namespace/the
")

(update-and-make-local-copy! #'clojure.core/butlast
  "Return a seq of all but the last item in coll, in linear time.
   If you are working on a vector and want the result to be a vector, use `pop`.")

    
(update-and-make-local-copy! #'clojure.core/every-pred
   "Take N functions and produce a single predicate - call it `result`. `result`
    will return `true` iff each of the original functions returns a truthy 
    value. `result` evaluates the functions in order, and the first one that produces
    a falsey value stops any further checking. In that case, `false` is returned.
    
    See also [[some-fn]].
    
         ( (every-pred integer? even? pos?) \"hi\") => false
         ( (every-pred integer? even? pos?) 4) => true
")

    
(update-and-make-local-copy! #'clojure.core/some-fn
   "Take N functions and produce a single function - call it `result`. `result`
    evaluates the N functions in order. When one returns a truthy result, `result`
    skips the remaining functions and returns that result. If none of the functions
    returns a truthy value, `result` returns a falsey value.
    
    This function is called `some-fn` because it does not produce a pure (true/false)
    result. See also [[every-pred]].
    
         ( (some-fn even? pos?) 3) => true
         ( (some-fn even? pos?) -3) => false
         
         ( (some-fn second first) [1 2 3]) => 2
         ( (some-fn second first) [1]) => 1
         ( (some-fn second first) []) => nil
")

    
(update-and-make-local-copy! #'clojure.core/some
   "Apply `pred` to each element in `coll` until it returns a truthy value,
    then return that value (*not* the element). This can be used to ask 
    the question \"is there an even value in the collection?\":

        (some even? [1 2 3 4]) => true

    However, as signaled by the lack of a '?' in `some`, its value is not
    necessarily a boolean. Here is how you would ask \"is there a value 
    greater than zero and, if so, what is the first of them?\":

        (some #(and (pos? %) %) [-1 -3 2 4]) => 2

    `some` is often used to ask the question \"is X in the collection?\".
    That takes advantage of how sets can be treated as a \"contains value?\"
    function:
    
        (#{1 2 3} 2) => 2
        (#{1 2 3} -88) => nil

    So:
    
        (some #{2} [1 2 3]) => 2
        (some #{2} [-1 -2 -3]) => nil

    You may find [[any?]] (in `such.shorthand`) easier to remember.
    
    It's easy to forget that `some` returns the *value of the predicate*,
    not the element itself. To be sure you get the element, do this:
    
        (first (filter pred coll))
    
    That's [[find-first]] in `such.shorthand`.
")


(update-and-make-local-copy! #'clojure.core/sequential?
  "True of lazy sequences, vectors, and lists. False for other
   Clojure built-in types. Note: perhaps surprisingly, *not* true
   of strings and java arrays.
   Any new type can be `sequential?` if it implements the Java 
   interface `Sequential`, a marker interface that has no methods.")

(update-and-make-local-copy! #'clojure.core/cond->
  "The `clauses` have two parts: a *test expression* and a *form*.
   An entire `cond->` expression looks like this:
   
        (cond-> <expr>
                <independent-test-1> <exec-1>
                <independent-test-2> <exec-2>
                ...)
   
   The independent tests do *not* have the value of `expr` threaded
   into them. If, however, `independent-test-1` is truthy, the 
   value of `expr` will be threaded into `exec-1`, using the rules of `->`.
   The resulting value will be threaded into the value of `exec2` 
   when `independent-test-2` is false.
   
   Examples will clarify. Here is a `cond->` form that threads through
   each of the `exec` forms:
   
       (cond-> 1
               true inc
               true inc
               true inc)
       => 4
   
   Here's an example that illustrates that the original `expr` has *nothing to do*
   with the tests:
   
       (cond-> 1
               string? inc
               string? inc
               string? inc)
       => 4

    The result is 4 because `string?`, a function, is a truthy value.
    
    Here's an example of a function that takes arguments describing which 
    branches to take:
    
       (defn brancher [& branches]
         (letfn [(take? [n] (contains? (set branches) n))]
           (cond-> []
                   (take? 1) (conj 1)
                   (take? 2) (conj 2)
                   (take? 3) (conj 3))))
       
       (brancher 1 3) => [1 3]
")
   
(update-and-make-local-copy! #'clojure.core/cond->>
  "This is like [[cond->]], except that values are threaded into the
   last position, as with `->>`.
   
        (cond->> [1 2 3]
                false (map inc) ; not taken
                true  (map -))
        => [-1 -2 -3]")
