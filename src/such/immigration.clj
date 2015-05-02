(ns such.immigration
  "Functions useful for constructing a \"favorite functions\" namespace that you can
   use as a supplement to `clojure.core`. That namespace is typically constructed from 
   various other utility namespaces that you don't want to require individually.
   You can `:require :as` the supplementary namespaces, or - more daringly - you can 
   `:use` or `:require :refer :all` it. 
   See [`such.clojure.core`](https://github.com/marick/suchwow/blob/master/test/such/clojure/core.clj) 
   and [`such.clojure.more-core`](https://github.com/marick/suchwow/blob/master/test/such/clojure/more-core.clj)
   for examples."
  (:use such.versions)
  (:require [such.vars :as var]
            [such.casts :as cast]
            [such.symbols :as symbol]))

(defn- move-var! [var sym]
  (ns-unmap *ns* sym)
  (when (var/has-root-value? var)
    (intern *ns*
            (with-meta sym (meta var))
            (var/root-value var))))

(defn namespaces
  "Create (`intern`) a public var in the current namespace for each public var in the `ns-names`.
  The created vars have the same name, root value, and metadata as the original
  (except for the :ns metadata value, which is the current namespace).

  The `ns-names` are typically symbols like `'such.immigration`, but they may also be
  strings, keywords, or even namespaces themselves. (See [[as-ns-symbol]].)

      (immigrate/namespaces 'such.types 'such.casts)
      (immigrate/namespaces :such.types (find-ns 'such.casts))

  Namespaces are loaded in left-to-right order, so a var defined in
  two namespaces ends up with values from the final one. This is
  useful when loading namespaces that improve on clojure.core:

      (immigrate/namespaces 'such.better-doc   ; just bettter documentation for core fns
                            'such.wide-domains) ; wider domains *and* better documentation.

  Existing vars, whether actually present (`intern`) or included by reference (`refer`)
  are silently overwritten.
"

  [& ns-names]
  (doseq [ns (map cast/as-ns-symbol ns-names)]
    (require ns)
    (doseq [[sym ^clojure.lang.Var var] (ns-publics ns)]
      (move-var! var sym))))

(defn namespaces-by-reference
  "This is `(require ns :refer :all)` for each of the namespaces, except:
   
   1. The `ns-names` can be strings, keywords, symbols or namespaces (see [[as-ns-symbol]]).
   
   2. Existing references will be overwritten without a warning.

   This function is useful for \"using\" a namespace that contains overrides of 
   `clojure.core` functions. (Such as `such.better-doc` and `such.wide-domains`.)
   See [`such.clojure.more-core`](https://github.com/marick/suchwow/blob/master/test/such/clojure/more-core.clj) for an example.
"

  [& ns-names]
  (doseq [ns (map cast/as-ns-symbol ns-names)]
    (require ns)
    (doseq [[sym ^clojure.lang.Var var] (ns-publics ns)]
      (ns-unmap *ns* sym))
    (refer ns)))

(defn selection
  "For each `var-name` that corresponds to a var in `ns`,
   create a var in this namespace with the same name, root binding,
   and metadata.

   `ns` must be accepted by [[as-ns-symbol]] (namespace, symbol, or string).
   `var-names` must be accepted by [[as-symbol-without-namespace]]
   (symbol, var, keyword, or string).
   It is an Exception for there to be no var in `ns` corresponding to a `var-name`.

        (immigrate/selection 'such.casts '[as-ns-symbol as-symbol-without-namespace])
"
  [ns var-names]
  (let [true-ns (cast/as-ns-symbol ns)]
    (doseq [var-symbol (map cast/as-symbol-without-namespace var-names)]
      (move-var! (ns-resolve true-ns var-symbol) var-symbol))))

(defn prefixed
  "Immigrate each public var in `ns`, but prefix their names in this namespace
   with the given `prefix`.

   `ns` must be accepted by [[as-ns-symbol]] (namespace, symbol, or string).
   `prefix` may be a symbol, string, or keyword.

        (immigrate/prefixed 'clojure.string \"str-\")
        (str-join \"-\" [\"a\" \"b\"]) => \"a-b`\"
"
  [ns prefix]
  (let [true-ns (cast/as-ns-symbol ns)]
    (doseq [[sym var] (ns-publics true-ns)]
      (move-var! (ns-resolve true-ns sym)
                 (symbol/from-concatenation (vector prefix sym))))))
