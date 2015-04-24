(ns such.casts
  (:use such.types))

;; Util
(defn- as-ns-symbol
  "If the arg is a namespace, cast it to a symbol naming it.
   A symbol arg is returned unchanged.
   In all other cases, an Exception is thrown.
   Use with namespace functions that require a symbol (find-ns, etc.)"
  [ns-or-symbol]
  (cond (namespace? ns-or-symbol) (ns-name ns-or-symbol)
        (symbol? ns-or-symbol) symbol
        :else (throw (new Exception "`ns-name` takes a symbol or namespace"))))

(defn- as-var-name-symbol
  "If the arg is a var, cast it to a symbol with the same name.
   Note that the symbol will not be namespace-qualified.
   If the arg is a symbol, a symbol without any namespace qualification is returned.
   In all other cases, an Exception is thrown.
   Use with namespace functions that require a symbol (ns-resolve, etc.)"
  [symbol-or-var]
  (if (symbol? symbol-or-var)
    symbol-or-var
    (:name (meta symbol-or-var))))


