(ns such.util.fail)

(defn fail [fmt & args]
  (throw (new Exception (apply format fmt args))))

(def bad-arg-type
  (partial fail "Bad argument type for `%s`: %s."))
