(ns ^{:doc "Which version of Clojure am I running in?"}
  such.versions)

(def ^:private minor (:minor *clojure-version*))

(defmacro when=1-4 [& body] 
  (when (=  minor 4)
    `(do ~@body)))

(defmacro when<=1-5 [& body] 
  (when (<= minor 5)
    `(do ~@body)))

(defmacro when>=1-5 [& body] 
  (when (>= minor 5)
    `(do ~@body)))

(defmacro when>=1-6 [& body] 
  (when (>= minor 6)
    `(do ~@body)))
