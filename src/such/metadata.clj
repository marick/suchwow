(ns such.metadata
  "Convenience functions for working with metadata. Intended to be used with
  `(:require [such.metadata :as meta])`."
  (:refer-clojure :exclude [merge assoc get]))

(defn get
  "Equivalent to `(get (meta o) k)` or `(get (meta o) k default)`."
  ([o k default]
     (clojure.core/get (meta o) k default))
  ([o k]
     (get o k nil)))

(defn merge
  "Merge the maps onto the metadata of `o`, creating a new object
  equal to `o` but with the merged metadata.
  
      (meta/merge o {:author \"Brian\" :lang :en-ca})
"
  [o & maps]
  (let [all (apply clojure.core/merge maps)]
    (vary-meta o clojure.core/merge all)))

(defn assoc
  "`assoc` the key-value pairs onto the metadata of `o`, creating a
  new object equal to `o` but with the new metadata.
  
      (meta/assoc o :author \"Brian\" :lang :en-ca)
"
  [o & kvs]
  (let [all (apply hash-map kvs)]
    (merge o all)))

