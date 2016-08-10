(ns such.immigration
  "[Potemkin](https://github.com/ztellman/potemkin)'s `import-vars` 
   is the most reliable way I know to make a namespace that gathers vars from several
   namespaces and presents them as a unified API. This namespace builds
   on it. 
   See [the tests](https://github.com/marick/suchwow/blob/master/test/such/f_immigration.clj)
   and [commons.clojure.core](https://github.com/marick/clojure-commons/blob/master/src/commons/clojure/core.clj)
   for two examples of creating a \"favorite functions\" namespace that
   can be included everywhere with (for example) `(ns my.ns (:use my.clojure.core))`.
"
  (:require [potemkin.namespaces :as potemkin]
            [such.symbols :as symbol]
            [such.ns :as ns]
            [such.vars :as var]
            [such.control-flow :as flow]))

(defn warning-require [sym]
  (when-not (find-ns sym)
    (binding [*out* *err*]
      (println (format "-------- WARNING for %s" (ns-name *ns*)))
      (println (format "You should include `%s` in your `ns` form." sym))
      (println "Currently, you are not required to, but such behavior is ")
      (println "DEPRECATED and will be removed in a future release.")
      (println "The problem is that code without the `ns` declaration")
      (println "does not work with uberjars - and fails in puzzling way.")))
  (require sym))

;; I hope to remove this fairly soon.
(defmacro ^:no-doc next-version-potemkin-import-vars
  "Imports a list of vars from other namespaces."
  [& syms]
  (let [unravel (fn unravel [x]
                  (if (sequential? x)
                    (->> x
                         rest
                         (mapcat unravel)
                         (map
                          #(symbol
                            (str (first x)
                                 (when-let [n (namespace %)]
                                   (str "." n)))
                            (name %))))
                    [x]))
        syms (mapcat unravel syms)]
    `(do
       ~@(map
          (fn [sym]
            (let [vr (resolve sym)
                  m (meta vr)]
              (cond
               (nil? vr) `(throw (ex-info (format "`%s` does not exist" '~sym) {}))
               (:macro m) `(potemkin/import-macro ~sym)
               (:arglists m) `(potemkin/import-fn ~sym)
               :else `(potemkin/import-def ~sym))))
          syms))))

(defmacro import-vars
  "Import named vars from the named namespaces and make them (1) public in this
   namespace and (2) available for `refer` by namespaces that require this one.
   See [Potemkin](https://github.com/ztellman/potemkin) for more.
   
        (import-vars [clojure.math.combinatorics
                         count-permutations permutations]
                     [clojure.data.json
                         write-str])
   "
  [& namespace-and-var-descriptions]
  (let [namespaces (map first namespace-and-var-descriptions)
        requires (map (fn [ns] `(require '~ns)) namespaces)]
    (doseq [ns namespaces] (warning-require ns))
    `(next-version-potemkin-import-vars ~@namespace-and-var-descriptions)))


(defmacro import-all-vars
  "Import all public vars from the namespaces, using Potemkin's
   `import-vars`.
    
          (import-all-vars clojure.set) ; note namespace is unquoted.

"
  [& ns-syms]
  (let [expand (fn [ns-sym]
                 (warning-require ns-sym)
                 (into (vector ns-sym) (keys (ns-publics ns-sym))))
        expanded (map #(list `import-vars (expand %)) ns-syms)]
    `(do ~@expanded)))

(defmacro import-prefixed-vars
  "Import all public vars from the namespace, using Potemkin's `import-vars`.
   Within the current namespace, the imported vars are prefixed by `prefix`,
   a symbol.
    
        (import-prefixed-vars clojure.string str-) ; note lack of quotes
        (str-trim \"  f \") => \"f\"
"
  [ns-sym prefix]
  (letfn [(one-call [[unqualified var]]
            (let [qualified (symbol/+symbol ns-sym unqualified)
                  to (symbol/from-concatenation [prefix unqualified])
                  importer (flow/branch-on var
                              var/has-macro?     `potemkin/import-macro
                              var/has-function?  `potemkin/import-fn
                              :else              `potemkin/import-def)]
              `(do
                 (~importer ~qualified ~to)
                 (alter-meta! (ns/+find-var '~to) #(dissoc % :file :line :column)))))]
    (warning-require ns-sym)
    `(do
       ~@(map one-call (ns-publics ns-sym)))))

(defn ^:no-doc namespaces
  [& ns-names]
  (println "`namespaces` has been removed in favor of potemkin/import-vars"))

(defn ^:no-doc namespaces-by-reference
  [& ns-names]
  (println "`namespaces-by-reference` has been removed in favor of potemkin/import-vars"))

(defn ^:no-doc selection
  [ns var-names]
  (println "`selection` has been removed in favor of potemkin/import-vars"))

(defn ^:no-doc prefixed
  [ns prefix]
  (println "`selection` has been removed in favor of potemkin/import-vars"))
