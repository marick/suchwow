(ns such.immigration
  "Use [Potemkin](https://github.com/ztellman/potemkin) `import-vars` to 
   make a \"favorite functions\" namespace that  supplements `clojure.core`. 
   This namespace contains useful macros that build on it. It also imports
   the Potemkin functions for convenience."
  (:require [potemkin.namespaces :as ns]))

(ns/import-vars [potemkin.namespaces
                 import-fn
                 import-macro
                 import-def
                 import-vars])




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
