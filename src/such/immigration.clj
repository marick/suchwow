(ns such.immigration
  "Functions useful for constructing a \"favorite functions\" namespace that you can
   use as a supplement to `clojure.core` (with `:use` or `:require :all`). 
   See [`such.clojure.core`](https://github.com/marick/suchwow/blob/master/test/such/clojure/core.clj) for an example."
  (:use such.versions)
  (:require [such.vars :as var]
            [such.casts :as cast]
            [such.symbols :as symbol]))

(defn- move-var! [var sym]
  (when (var/has-root-value? var)
    (ns-unmap *ns* sym)
    (intern *ns*
            (with-meta sym (meta var))
            (var/root-value var))))

(defn namespaces
  "Create a public var in the current namespace for each public var in the `ns-names`.
  The created vars have the same name, root value, and metadata as the original
  (except for the :ns metadata value, which is this namespace).

  The names are typically symbols like `such.immigration`, but they may also be strings
  or namespaces themselves. (See [[as-ns-symbol]].)

      `(immigrate/namespaces 'such.types 'such.casts)`
"

  [& ns-names]
  (doseq [ns (map cast/as-ns-symbol ns-names)]
    (require ns)
    (doseq [[sym ^clojure.lang.Var var] (ns-publics ns)]
      (move-var! var sym))))

(defn selection
  "For each `var-name` that corresponds to a var in `ns`,
   create a var in this namespace with the same name, root binding,
   and metadata.

   `ns` must be accepted by [[as-ns-symbol]] (namespace, symbol, or string).
   `var-names` must be accepted by [[as-var-name-symbol]] (symbol, var, or string).
   It is an Exception for there to be no var in `ns` corresponding to a `var-name`.

        `(immigrate/selection 'such.casts '[as-ns-symbol as-var-name-symbol])`
"
  [ns var-names]
  (let [true-ns (cast/as-ns-symbol ns)]
    (doseq [var-symbol (map cast/as-var-name-symbol var-names)]
      (move-var! (ns-resolve true-ns var-symbol) var-symbol))))

(defn prefixed
  "Immigrate each public var in `ns`, but prefix their names in this namespace
   with the given `prefix`.

   `ns` must be accepted by [[as-ns-symbol]] (namespace, symbol, or string).
   `prefix` may be a symbol, string, or keyword.

        `(immigrate/prefixed 'clojure.string \"str-\")`
"
  [ns prefix]
  (let [true-ns (cast/as-ns-symbol ns)]
    (doseq [[sym var] (ns-publics true-ns)]
      (move-var! (ns-resolve true-ns sym)
                 (symbol/from-concatenation (vector prefix sym))))))
