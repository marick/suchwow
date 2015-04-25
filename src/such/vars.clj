(ns such.vars)

(defprotocol Rootable
  "A protocol to look at original values of Vars."
  (has-root-value? [this]
    "Was this given an original value when it was created?" )
  (root-value [this]
    "What value was originally assigned. (Ignores any bindings in effect.)"))

(extend-type clojure.lang.Var
  Rootable
  (has-root-value? [var] (.hasRoot var))
  (root-value [var] (alter-var-root var identity)))

(defn name-as-symbol [var]
  (:name (meta var)))

(defn name-as-string [var]
  (str (name-as-symbol var)))
