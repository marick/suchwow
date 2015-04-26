(ns such.ns
  "Makes working with namespaces easier."
  (:use such.types)
  (:require [such.casts :as cast]))

(defmacro with-scratch-namespace 
  "Create a scratch namespace named `ns-name`, run `body` within it, then 
   remove it. `ns-name` *must* be a symbol. If the namespace already
   exists, it will be removed, then recreated, then removed."
  [ns-sym & body]
  (when (and (sequential? ns-sym)
             (= (first ns-sym) 'quote))
    (println "You quoted the `ns-sym` arg to `with-scratch-namespace`. Don't do that."))
  `(try
     (remove-ns '~ns-sym)
     (create-ns '~ns-sym)
     ~@body
   (finally
     (remove-ns '~ns-sym))))
