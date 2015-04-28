(ns such.wide-domains
  "Variants of clojure.core functions that accept more types of inputs."
  (:refer-clojure :exclude [find-var symbol])
  (:require [such.casts :as cast]
            [clojure.string :as str]
            [such.util.fail :as fail]))


(defn symbol
  "Creates a symbol.    
  The `ns` argument may be a namespace, symbol, or string ([[as-ns-string]]).    
  The `name` argument may be a symbol, string, keyword, or var.

  In the one-argument version, the resulting symbol has a `nil` namespace.
  In the two-argument version, it has the symbol version of `ns` as the namespace.
  Note that `ns` need not refer to an existing namespace.

      (symbol \"th\") => 'th
      (symbol 'clojure.core \"th\") => 'clojure.core/th

      (symbol *ns* 'th) => 'this.namespace/th ; \"add\" a namespace
      (symbol *ns* 'clojure.core/even?) => 'this.namespace/even? ; \"localize\" a symbol.
"
([name]
 (clojure.core/symbol (cast/as-name-string name)))
([ns name]
  (clojure.core/symbol (str (cast/as-ns-symbol ns)) (cast/as-name-string name))))

(defn find-var 
  ([namelike]
     (cond (symbol? namelike) (clojure.core/find-var namelike)
           (var? namelike) namelike
           :else (find-var (apply symbol (cast/as-namespace-and-name namelike))))))
