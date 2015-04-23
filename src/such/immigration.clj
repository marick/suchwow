(ns such.immigration
  (:use such.versions)
  (:require [such.vars :as vars]))


(defn- move-var! [var sym]
  (when (vars/has-root-value? var)
    (ns-unmap *ns* sym)
    (intern *ns*
            (with-meta sym (meta var))
            (vars/root-value var))))

(defn namespaces
  "Create a public var in this namespace for each public var in the
  namespaces named by ns-names. The created vars have the same name, root
  binding, and metadata as the original except that their :ns metadata
  value is this namespace."
  [& ns-names]
  (doseq [ns ns-names]
    (require ns)
    (doseq [[sym ^clojure.lang.Var var] (ns-publics ns)]
      (move-var! var sym))))

(defn selection
  "For each symbol in `symbols` that corresponds to a var in `ns`,
   create a var in this namespace with the same name, root binding,
   and metadata."
  [ns symbols]
  (doseq [sym symbols]
    (move-var! (ns-resolve ns sym) sym)))

(defn prefixed
  "Immigrate each public var in `ns`, but prefix their names in this namespace
   with the given `prefix`."
  [ns prefix]
  (doseq [[sym var] (ns-publics ns)]
    (move-var! (ns-resolve ns sym) (symbol (str prefix (name sym))))))
