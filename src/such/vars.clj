(ns such.vars
  "Common operations on vars.")

(defprotocol Rootable
  "A protocol to look at original values of Vars."
  (has-root-value? [this]
    "Was this var given an original value when it was created?" )
  (root-value [this]
    "What value was originally assigned to this var? (Ignores any bindings in effect.)"))

(extend-type clojure.lang.Var
  Rootable
  (has-root-value? [var] (.hasRoot var))
  (root-value [var] (alter-var-root var identity)))

(defn name-as-symbol 
  "Unlike symbols and keywords, the \"name\" of a var is a symbol. This function
   returns that symbol. See also [[name-as-string]].
   
        (var/name-as-symbol #'clojure.core/even?) => 'even?)

   Note that the symbol does not have a namespace."
  [var]
  (:name (meta var)))

(defn name-as-string
  "Unlike symbols and keywords, the \"name\" of a var is a symbol. This function
   returns the string name of that symbol. See also [[name-as-symbol]].

        (var/name-as-string #'clojure.core/even?) => \"even?\")
"
  [var]
  (name (name-as-symbol var)))
