(ns such.immigration
  "[Potemkin](https://github.com/ztellman/potemkin)'s `import-vars` 
   is the most reliable way I know to make a namespace that gathers vars from several
   namespaces and presents them as a unified API. This namespace builds
   on it. 
   See [the tests](https://github.com/marick/suchwow/blob/master/test/such/f_immigration.clj)
   and [commons.clojure.core](https://github.com/marick/clojure-commons/blob/master/src/commons/clojure/core.clj)
   for two examples of creating a \"favorite functions\" namespace that
   can be included everywhere with (for example) `(ns my.ns (:use my.clojure.core))`.
"
  (:require [potemkin.namespaces :as ns]
            [such.symbols :as symbol]
            [such.vars :as var]
            [such.control-flow :as flow]))

(ns/import-vars [potemkin.namespaces
                 import-vars])

(defmacro import-vars
  "Import named vars from the named namespaces and make them (1) public in this
   namespace and (2) available for `refer` by namespaces that require this one.
   See [Potemkin](https://github.com/ztellman/potemkin) for more.
   
        (import-vars [clojure.math.combinatorics
                         count-permutations permutations]
                     [clojure.data.json
                         write-str])
    
   This version differs from Potemkin's in that you needn't have already required
   the namespaces you're importing."
  [& namespace-and-var-descriptions]
  (let [namespaces (map first namespace-and-var-descriptions)
        requires (map (fn [ns] `(require '~ns)) namespaces)]
    `(do 
       ~@requires
       (ns/import-vars ~@namespace-and-var-descriptions))))


(defmacro import-all-vars
  "Import all public vars from the namespace, using Potemkin's
   `import-vars`.
    
          (import-all-vars clojure.set) ; note namespace is unquoted.

"
  [ns-sym]
  (let [expanded (into (vector ns-sym) (keys (ns-publics ns-sym)))]
    `(import-vars ~expanded)))

(defmacro import-prefixed-vars
  "Import all public vars from the namespace, using Potemkin's `import-vars`.
   Within the current namespace, the imported vars are prefixed by `prefix`,
   a symbol.
    
        (import-prefixed-vars clojure.string str-) ; note lack of quotes
        (str-trim \"  f \") => \"f\"
"
  [ns-sym prefix]
  (letfn [(one-call [[unqualified var]]
            (let [qualified (symbol/+symbol ns-sym unqualified)
                  to (symbol/from-concatenation [prefix unqualified])
                  importer (flow/branch-on var
                              var/has-macro?     `ns/import-macro
                              var/has-function?  `ns/import-fn
                              :else              `ns/import-def)]
              `(~importer ~qualified ~to)))]
    `(do
       (require '~ns-sym)
       ~@(map one-call (ns-publics ns-sym)))))

(defn ^:no-doc namespaces
  [& ns-names]
  (println "`namespaces` has been removed in favor of potemkin/import-vars"))

(defn ^:no-doc namespaces-by-reference
  [& ns-names]
  (println "`namespaces-by-reference` has been removed in favor of potemkin/import-vars"))

(defn ^:no-doc selection
  [ns var-names]
  (println "`selection` has been removed in favor of potemkin/import-vars"))

(defn ^:no-doc prefixed
  [ns prefix]
  (println "`selection` has been removed in favor of potemkin/import-vars"))
