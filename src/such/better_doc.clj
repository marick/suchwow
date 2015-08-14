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
   when `independent-test-2` is truthy.
   
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

(update-and-make-local-copy! #'clojure.core/reduce
  "`reduce` converts a collection into a single value. Except for 
   one exception (described below), `f` must take two arguments.
   `+` takes two arguments, so it can be used to reduce a collection
   of numbers to their sum:
   
        (reduce + 0 [1 2 3 4]) => 10
   
   `+` is called four times. Here is the sequence of calls.
   
        (+ 0 1) => 1   ; sum of first element
        (+ 1 2) => 3   ; sum of first two elements
        (+ 3 3) => 6   ; sum of first three elements
        (+ 6 4) => 10  ; ...
   
   At any point, the first argument to `f` is the \"reduction\" of all
   the previous calls to `f`, and the second argument is the next
   collection element to add into the reduction. When defining functions to
   use with `reduce`, the first argument is often called `acc` (for
   \"accumulator\") or `so-far`.
   
   When using `+`, the distinction between the two arguments isn't
   clear, so here's an example that returns the longest string in a
   collection:
   
        (reduce (fn [max-so-far elt]
                  (if (> (count elt) max-so-far)
                    (count elt)
                    max-so-far))
                0
                [\"abc\" \"ab\" \"abcd\" \"a\"]) => 4
   
   When you're surprised by the results of a call to `reduce`, you 
   can use [[reductions]] as an easy way to see what's going on:
   
        (reductions + 0 [1 2 3 4]) 
                  =>    (0 1 3 6 10)
   
   `reductions` returns a collection of all the first arguments to `f` 
   plus the final result.

   Reduce is lazy, so the reduction isn't done until the result is used.
   
   The **two-argument form** can be used when `(f val (first coll))`
   is the same as `(first coll)`. That's the case with `+`, where
   `(+ 0 1)` is `1`. So, in the two argument form, the first call to `f`
   uses the first argument of the collection as the starting reduction
   and begins working with the second element:
   
        (reduce + [1 2 3 4]) => 10
        (reductions + [1 2 3 4]) => (1 3 6 10) ; slightly different result
   
   **Small arrays:** `f` takes two values. What if there aren't two values
   to give it? There's one special case
   for the three-argument form:
   
        (reduce + 0 []) => 0
        (reductions + 0 []) => (0)
   
   In this case, `val` is returned and `f` is never called.
   
   There are two special two-argument cases. The first is when there's
   only one element in the collection:
   
        (reduce + [10]) => 10
   
   The handling is really the same as the above, since the first argument in
   the collection is treated as the starting `val`. More interesting is the 
   empty collection:
   
        (reduce + []) => 0
        (reductions + []) => (0)
   
   Here, `f` *is* called, but with zero arguments. It happens that `(+)` is `0`.
   In the longest-string example, though, the result is not so pretty:
   
        (reduce (fn [max-so-far elt]...) [])
        ArityException Wrong number of args (0) ...
   
   **See also:** [[reductions]], [[reduce-kv]]
")

(update-and-make-local-copy! #'clojure.core/reductions
   "Perform a [[reduce]] but don't return only the final reduction. 
   Return a sequence of the intermediate reductions, ending with the 
   final reduction.
   
   Consider this reduction that produces nested vectors:
   
        (reduce (fn [so-far elt] (vector (conj so-far elt)))
                []
                [1 2 3 4 5])
        => [[[[[[1] 2] 3] 4] 5]]
   
   Using `reductions` instead:
   
        (reductions (fn [so-far elt] (vector (conj so-far elt)))
                    []
                    [1 2 3 4 5])
        => ([]
            [[1]]
            [[[1] 2]]
            [[[[1] 2] 3]]   [[[[[1] 2] 3] 4]]   [[[[[[1] 2] 3] 4] 5]])
   
   To be more precise, the result is a sequence of the first arguments to
   `f`, followed by the final value. Those are slightly different in the two-argument
   and three-argument cases:
   
        (reductions + 0 [1 2 3]) => (0 1 3 6)
        (reductions +   [1 2 3]) => (  1 3 6)
   
   The sequence is lazy:
   
        (take 3 (reductions (fn [so-far elt] (vector (conj so-far elt)))
                            []
                            (range)))  ; infinite list here
        => ([]  [[0]]  [[[0] 1]])
")

(update-and-make-local-copy! #'clojure.core/reduce-kv
  "For maps, `reduce-kv` is a trivial variant on [[reduce]]. The following
   two functions are the same:
   
       (reduce     (fn [so-far [key val]] ...) ... {...}
       (reduce-kvs (fn [so-far  key val ] ...) ... {...}

   For vectors, `reduce-kv` is to `reduce` as [[map-indexed]] is to `map`: 
   it provides indexes as well as collection elements. The argument list
   is `[reduction-so-far index element]`.
   
   This example sums up the indices and values of a vector:
   
       (reduce-kv + 0 [-1 -2 -3 -4]) => -4

   It works because `+` allows three arguments. Here's an example that converts
   a vector to a map whose keys are the vector indexes:

       (reduce-kv (fn [acc i elt] (assoc acc i elt))
                  {}
                  [\"0\" \"2\" \"3\" \"4\"])
       => {0 \"0\", 1 \"2\", 2 \"3\", 3 \"4\"}
   
   If `coll` is empty, `init` is returned and `f` is not called.
")


(update-and-make-local-copy! #'clojure.core/map-indexed
  "In the two-argument form, the following two are equivalent:
   
        (map-indexed f coll)
        (map f (range) coll)

   Thus, `f` should accept two arguments: the index of an element in `coll`
   and the element itself.
   
        (map-indexed vector [:a :b :c]) => ([0 :a] [1 :b] [2 :c])

   Like `map`, `map-indexed` is lazy.
   
   The single argument form produces a transducer.
")
