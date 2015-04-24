(ns such.ns
  (use such.types))

(defmacro with-scratch-namespace [& body]
  `(try
     (create-ns 'scratch.namespace)
     ~@body
   (finally
     (remove-ns 'scratch.namespace))))


