(ns such.relational
  "Given vectors of uniform maps, do some simple relational things to them.

   A work in progress. Note: will eventually contain the relational parts of
   clojure.set, with better documentation.

   This is only available for Clojure 1.6 and later."
  (:require [clojure.set :as set]
            [such.maps :as map]
            [such.imperfection :refer :all]
            [such.metadata :as meta]))

(defn- force-sequential [v]
  (if (sequential? v) v (vector v)))

(defn one-to-one-index-on [table one-key]
  (-> table
      (set/index (vector one-key))
      (meta/assoc ::type :one-to-one
                  ::value-handler first
                  ::key-maker #(hash-map one-key %))))

(defn compound-to-one-index-on [table keyseq]
  (-> table
      (set/index keyseq)
      (meta/assoc ::type :compound-to-one
                  ::value-handler first
                  ::key-maker #(apply hash-map (interleave keyseq %)))))

(defn- add-prefix [kvs prefix]
  (letfn [(prefixer [k]
            (-> (str (name prefix) (name k))
                (cond-> (keyword? k) keyword)))]
    (let [translation (apply hash-map
                             (interleave (keys kvs)
                                         (map prefixer (keys kvs))))]
      (set/rename-keys kvs translation))))


(defn select-map
  ([key options]
     (assert (contains? options :using) "You must provide an index with `:using`.")
     (let [index (get options :using)
           key-maker (meta/get index ::key-maker)
           value-handler (meta/get index ::value-handler)
           desired-keys (get options :only)
           prefix (get options :prefix)]
       (-> index
           (get (key-maker key))
           value-handler
           (cond-> desired-keys (select-keys desired-keys))
           (cond-> prefix (add-prefix (name prefix))))))
  ([key k v & rest] ; k and v are to give this different arity than above
     (select-map key (apply hash-map k v rest))))


(defn extend-map
  ([kvs options]
     (assert (contains? options :using) "You must provide an index with `:using`.")
     (assert (contains? options :via) "You must provide an index with `:using`.")

     (let [foreign-key-value (get kvs (:via options))]
       (merge kvs (select-map foreign-key-value options))))

  ([kvs k v & rest] ; k and v are to give this different arity than above
     (extend-map kvs (apply hash-map k v rest))))




;; (defn- one-to-N-index-on [one-key combining-fn maps prefix]
;;   (let [result (reduce (fn [so-far kvs]
;;                          (combining-fn so-far (get kvs one-key) kvs))
;;                        {}
;;                        maps)]
;;         (cond-> result prefix (meta/assoc ::prefix (name prefix)))))

;; (defn one-to-one-index-on
;;   "Given a list of `maps` with a \"primary\" (unique) key,
;;    produce a map from the different values of the primary key to
;;    the original maps they identify.

;;           (one-to-one-index-on :pk [{:pk 1, :rest 2} {:pk 2, :rest 3}])
;;           => {1 {:pk 1, :rest 2}
;;               2 {:pk 2, :rest 3}}
;; "
;;   ([one-key maps]
;;      (one-to-one-index-on one-key maps nil))
;;   ([one-key maps prefix]
;;      (one-to-N-index-on one-key assoc maps prefix)))

;; (defn one-to-many-index-on
;;   ([one-key maps]
;;      (one-to-many-index-on one-key maps nil))
;;   ([one-key maps prefix]
;;      (one-to-N-index-on one-key map/conj-into maps prefix)))

;; (defn index-prefix [index]
;;   (meta/get index ::prefix))

;; (defn- rename-keys-according-to-index [index kvs]
;;   (let [prefix (index-prefix index)
;;         translation (apply hash-map (interleave (keys kvs)
;;                                                 (map #(->> % name (str prefix) keyword)
;;                                                      (keys kvs))))]
;;     (set/rename-keys kvs translation)))


;; (defn- merge-related-table*
;;   [this-map this-foreign-key-name that-index-on-foreign-key key-selector-fn]
;;   (->> (get this-map this-foreign-key-name)
;;        (get that-index-on-foreign-key)
;;        key-selector-fn
;;        (rename-keys-according-to-index that-index-on-foreign-key)
;;        (merge this-map)))



;; (defn merge-related-table
;;   ([this-map this-foreign-key-name that-index-on-foreign-key]
;;      (merge-related-table* this-map this-foreign-key-name that-index-on-foreign-key identity))

;;   ([this-map this-foreign-key-name that-index-on-foreign-key that-keys-wanted]
;;      (merge-related-table* this-map this-foreign-key-name that-index-on-foreign-key
;;                      #(select-keys % that-keys-wanted))))

;; (defn flattening-lookup [starting-value starting-index first-key-selection & descents]
;;   (reduce (fn [so-far [foreign-key relevant-index resulting-key-selection]]
;;             (merge-related-table so-far foreign-key relevant-index resulting-key-selection))
;;           (select-keys (get starting-index starting-value) first-key-selection)
;;           descents))
