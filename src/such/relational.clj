(ns such.relational
  (:require [clojure.set :as set]
            [such.metadata :as meta]))

(defn simple-index-on
  "Given a list of `maps` with a \"primary\" (unique) key,
   produce a map from the different values of the primary key to
   the original maps they identify.

          (simple-index-on :pk [{:pk 1, :rest 2} {:pk 2, :rest 3}])
          => {1 {:pk 1, :rest 2}
              2 {:pk 2, :rest 3}}
"
  ([primary-key maps]
     (reduce (fn [so-far kvs]
               (assoc so-far (get kvs primary-key) kvs))
             {}
             maps))

  ([primary-key maps prefix]
     (meta/assoc (simple-index-on primary-key maps)
                 ::prefix (name prefix))))

(defn index-prefix [index]
  (meta/get index ::prefix))

(defn- rename-keys-according-to-index [index kvs]
  (let [prefix (index-prefix index)
        translation (apply hash-map (interleave (keys kvs)
                                                (map #(->> % name (str prefix) keyword)
                                                     (keys kvs))))]
    (set/rename-keys kvs translation)))


(defn- has-one:merge* [this-map this-foreign-key-name that-index-on-foreign-key key-selector-fn]
  (->> (get this-map this-foreign-key-name)
       (get that-index-on-foreign-key)
       key-selector-fn
       (rename-keys-according-to-index that-index-on-foreign-key)
       (merge this-map)))



(defn has-one:merge
  ([this-map this-foreign-key-name that-index-on-foreign-key]
     (has-one:merge* this-map this-foreign-key-name that-index-on-foreign-key identity))

  ([this-map this-foreign-key-name that-index-on-foreign-key that-keys-wanted]
     (has-one:merge* this-map this-foreign-key-name that-index-on-foreign-key
                     #(select-keys % that-keys-wanted))))

(defn flattening-lookup [starting-value starting-index first-key-selection & descents]
  (reduce (fn [so-far [foreign-key relevant-index resulting-key-selection]]
            (has-one:merge so-far foreign-key relevant-index resulting-key-selection))
          (select-keys (get starting-index starting-value) first-key-selection)
          descents))
