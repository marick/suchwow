(ns ^:no-doc such.util.fail)

(defn fail [fmt & args]
  (throw (new Exception (apply format fmt args))))

(def not-namespace-and-name
  (partial fail "%s can't be interpreted as having a namespace and name"))

(def bad-arg-type
  (partial fail "Bad argument type for `%s`: %s."))

(def should-not-have-namespace
  (partial fail "`%s` should not be given a val with a namespace. %s has one."))

(def should-have-namespace
  (partial fail "`%s` should be given a val with a namespace. %s has none."))
