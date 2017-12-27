(ns such.wrongness)

(defn boom!
  "In the first case, throw a java.lang.Exception whose message was constructed
   by applying `format` to `fmt` and the `args`. In the second case, the exception
   thrown is given by `exception-class`.

       (boom! \"wow\")
       (boom! \"wow: %s\" (cons 1 (cons 2 nil)))
       (boom! NumberFormatException \"wow: %s\" input)"
  {:arglists '([fmt & args] [exception-class fmt & args])}
  [& args]
  (if (instance? java.lang.Class (first args))
    (let [[klass fmt & vals] args
          arglist-fmt        (into-array java.lang.Class [java.lang.String])
          constructor        (.getConstructor ^Class klass arglist-fmt)
          message            (apply format fmt vals)
          arglist            (into-array java.lang.String [message])
          exception          (.newInstance constructor arglist)]
      (throw exception))
    (apply boom! (cons Exception args))))

(def ^:no-doc not-namespace-and-name
  (partial boom! "%s can't be interpreted as having a namespace and name"))

(def ^:no-doc bad-arg-type
  (partial boom! "Bad argument type for `%s`: %s."))

(def ^:no-doc should-not-have-namespace
  (partial boom! "`%s` should not be given a val with a namespace. %s has one."))

(def ^:no-doc should-have-namespace
  (partial boom! "`%s` should be given a val with a namespace. %s has none."))
