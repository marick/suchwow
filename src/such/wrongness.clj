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
          constructor (.getConstructor klass (doto (make-array Class 1) (aset 0 String)))
          message (apply format fmt vals)
          exception (.newInstance constructor (doto (make-array String 1) (aset 0 message)))]
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
