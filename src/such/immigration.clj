(ns such.immigration
  "Use [Potemkin](https://github.com/ztellman/potemkin) `import-vars` to 
   make a \"favorite functions\" namespace that  supplements `clojure.core`. 
   This namespace contains useful macros that build on it. It also re-exports
   the Potemkin functions for convenience."
  (:require [potemkin.namespaces :as ns]
            [such.symbols :as symbol]
            [such.vars :as var]
            [such.control-flow :as flow]))

(ns/import-vars [potemkin.namespaces
                 import-fn
                 import-macro
                 import-def
                 import-vars])

(defmacro import-all-vars
  "Import all public vars from the namespace, using Potemkin's `import-vars`.
   
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
                  to (symbol (str prefix unqualified))
                  importer (flow/branch-on var
                              var/has-macro?     `ns/import-macro
                              var/has-function?  `ns/import-fn
                              :else              `ns/import-def)]
              `(~importer ~qualified ~to)))]
    `(do
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
