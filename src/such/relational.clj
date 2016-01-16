(ns such.relational
  "Given vectors of uniform maps, do some simple relational things to them.

   A work in progress. Note: will eventually contain the relational parts of
   clojure.set, with better documentation.

   This is only available for Clojure 1.6 and later."
  (:require [clojure.set :as set]
            [such.maps :as map]
            [such.metadata :as meta]))


(defn- one-to-N-index-on [one-key combining-fn maps prefix]
  (let [result (reduce (fn [so-far kvs]
                         (combining-fn so-far (get kvs one-key) kvs))
                       {}
                       maps)]
        (cond-> result prefix (meta/assoc ::prefix (name prefix)))))

(defn one-to-one-index-on
  "Given a list of `maps` with a \"primary\" (unique) key,
   produce a map from the different values of the primary key to
   the original maps they identify.

          (one-to-one-index-on :pk [{:pk 1, :rest 2} {:pk 2, :rest 3}])
          => {1 {:pk 1, :rest 2}
              2 {:pk 2, :rest 3}}
"
  ([one-key maps]
     (one-to-one-index-on one-key maps nil))
  ([one-key maps prefix]
     (one-to-N-index-on one-key assoc maps prefix)))

(defn one-to-many-index-on
  ([one-key maps]
     (one-to-many-index-on one-key maps nil))
  ([one-key maps prefix]
     (one-to-N-index-on one-key map/conj-into maps prefix)))

(defn index-prefix [index]
  (meta/get index ::prefix))

(defn- rename-keys-according-to-index [index kvs]
  (let [prefix (index-prefix index)
        translation (apply hash-map (interleave (keys kvs)
                                                (map #(->> % name (str prefix) keyword)
                                                     (keys kvs))))]
    (set/rename-keys kvs translation)))


(defn- merge-related-table*
  [this-map this-foreign-key-name that-index-on-foreign-key key-selector-fn]
  (->> (get this-map this-foreign-key-name)
       (get that-index-on-foreign-key)
       key-selector-fn
       (rename-keys-according-to-index that-index-on-foreign-key)
       (merge this-map)))



(defn merge-related-table
  ([this-map this-foreign-key-name that-index-on-foreign-key]
     (merge-related-table* this-map this-foreign-key-name that-index-on-foreign-key identity))

  ([this-map this-foreign-key-name that-index-on-foreign-key that-keys-wanted]
     (merge-related-table* this-map this-foreign-key-name that-index-on-foreign-key
                     #(select-keys % that-keys-wanted))))

(defn flattening-lookup [starting-value starting-index first-key-selection & descents]
  (reduce (fn [so-far [foreign-key relevant-index resulting-key-selection]]
            (merge-related-table so-far foreign-key relevant-index resulting-key-selection))
          (select-keys (get starting-index starting-value) first-key-selection)
          descents))
