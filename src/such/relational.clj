(ns such.relational
  "If you work with sequences of maps of the sort gotten by slurping
   up relational database tables or CVS files, here are some functions
   that allow you join-like behavior for one-to-one and one-to-many
   relationships.

   The core idea is that you create indexes for all the access patterns
   you use often - as you would with relational database tables - and
   use them to follow foreign keys and merge the results (approximating
   a join).

   A WORK IN PROGRESS. Note: will eventually contain the relational parts of
   clojure.set, with better documentation.

   This is only available for Clojure 1.7 and later."
  (:require [clojure.set :as set]
            [such.maps :as map]
            [such.imperfection :refer :all]
            [such.wrongness :refer [boom!]]
            [such.metadata :as meta]))

(defn- force-sequential [v]
  (if (sequential? v) v (vector v)))

(defn- mkfn:key-for-index
  "Given [:x :y], produce a function that takes [1 2] and
   returns {:x 1 :y 2}"
  [map-keys]
  (fn [map-values]
    (apply hash-map (interleave map-keys map-values))))

(defn- multi-get [kvs keyseq]
  "(multi-get {:x 1, :y 2, :z 3} [:x :z]) => [1 3]"
  (vals (select-keys kvs (force-sequential keyseq))))

(defn- prefix-all-keys [kvs prefix]
  (letfn [(prefixer [k]
            (-> (str (name prefix) (name k))
                (cond-> (keyword? k) keyword)))]
    (let [translation (apply hash-map
                             (interleave (keys kvs)
                                         (map prefixer (keys kvs))))]
      (set/rename-keys kvs translation))))

(defn- option-controlled-merge [old new options]
  (when (and (sequential? new)
             (not (contains? options :into)))
    (boom! "A merge using a one-to-many index must specify `:into`"))

  (if-let [destination (:into options)]
    (let [current (or (get old destination) [])
          extended (into current new)]
      (assoc old destination extended))
    (merge old new)))


(defn- one-to-one-index? [index]
  (= :one-to-one (meta/get index ::type)))
(defn- one-to-many-index? [index]
  (= :one-to-many (meta/get index ::type)))


;;;; Public

(defn- with-one-to-one-metadata [index keyseq]
  (meta/assoc index
              ::type :one-to-one
              ::value-handler first
              ::key-selector select-keys
              ::prefix-adder prefix-all-keys
              ::key-maker (mkfn:key-for-index keyseq)))

(defn one-to-one-index-on
  "`table` should be a sequence of maps. `keyseq` is either a single value
  (corresponding to a traditional `:id` or `:pk` entry) or a sequence of
  values (corresponding to a compound key).

  The resulting index provides fast access to individual maps.

      (def index:traditional (one-to-one-index-on table :id))
      (index-select 5 :using index:traditional :keys [:key1 :key2])

      (def index:compound (one-to-one-index-on table [\"intkey\" \"strkey\")))
      (index-select [4 \"dawn\"] :using index:compound)

  Note that keys need not be Clojure keywords.
  "
  [table keyseq]
  (if (sequential? keyseq)
    (-> table
        (set/index keyseq)
        (with-one-to-one-metadata keyseq))
    (one-to-one-index-on table [keyseq])))

(defn- with-one-to-many-metadata [index keyseq]
  (meta/assoc index
              ::type :one-to-many
              ::value-handler identity
              ::key-selector (fn [value keyseq]
                               (mapv #(select-keys % keyseq) value))
              ::prefix-adder (fn [value prefix]
                               (mapv #(prefix-all-keys % prefix) value))
              ::key-maker (mkfn:key-for-index keyseq)))


(defn one-to-many-index-on [table keyseq]
  "`table` should be a sequence of maps. `keyseq` is either a single value
  (corresponding to a traditional `:id` or `:pk` entry) or a sequence of
  values (corresponding to a compound key).

  The resulting index provides fast retrieval of vectors of matching maps.

      (def index:traditional (one-to-many-index-on table :id))
      (index-select 5 :using index:traditional :keys [:key-i-want]) ; a vector of maps

      (def index:compound (one-to-many-index-on table [\"intkey\" \"strkey\")))
      (index-select [4 \"dawn\"] :using index:compound) ; a vector of maps

  Keys may be either Clojure keywords or strings.
  "
  (if (sequential? keyseq)
    (-> table
        (set/index keyseq)
        (with-one-to-many-metadata keyseq))
    (one-to-many-index-on table [keyseq])))

(defn index-select
  "Produce a map by looking a key up in an index.

  See <<someplace>> for examples.

  `key` is a unique or compound key that's been indexed with [[one-to-one-index-on]]
  or [[one-to-many-index-on]]. The `options` may be given as N keys and values
  following `key` (Smalltalk style) or as a single map. They are:

  :using <index>
    (required) The index to use.
  :keys <[keys...]>
    (optional) Keys you're interested in (default is all of them)
  :prefix <prefix>
    (optional) Prepend the given prefix to all the keys in the selected map.
    The prefix may be either a string or keyword. The resulting key will be
    of the same type (string or keyword) as the original.

  The return value depends on the index. If it is `one-to-one`, a map is returned.
  If it is `one-to-many`, a vector of maps is returned.
  "
  ([key options]
     (assert (contains? options :using) "You must provide an index with `:using`.")
     (when-let [keys (options :keys)]
       (assert (vector? keys) ":keys takes a vector as an argument"))

     (let [index (get options :using)

           [key-maker value-handler key-selector prefix-adder]
           (mapv #(meta/get index %)
                 [::key-maker ::value-handler ::key-selector ::prefix-adder])

           [desired-keys prefix] (mapv #(get options %) [:keys :prefix])]

       (-> index
           (get (key-maker (force-sequential key)))
           value-handler
           (cond-> desired-keys (key-selector desired-keys))
           (cond-> prefix (prefix-adder prefix)))))
  ([key k v & rest] ; k and v are to give this different arity than above
     (index-select key (apply hash-map k v rest))))


(defn extend-map
  "Add more key/value pairs to `kvs`. They are found by looking up values
  in a [[one-to-one-index-on]] or [[one-to-many-index-on]] index.

  See <<someplace>> for examples.

  The `options` control what maps are returned and how they're merged into the
  original `kvs`. They may be given as N keys and values
  following `kvs` (Smalltalk style) or as a single map. They are:

  :using <index>
    (required) The index to use.
  :via <key>
    (required) A single foreign key or a sequence of them that is used to
    look up a map in the <index>.
  :into <key>
    (optional, relevant only to a one-to-many map). Since a one-to-many map
    can't be [[merge]]d into the `kvs`, it has to be added \"under\" (as the
    value of) a particular `key`.
  :keys <[keys...]>
    (optional) Keys you're interested in (default is all of them)
  :prefix <prefix>
    (optional) Prepend the given prefix to all the keys in the selected map.
    The prefix may be either a string or keyword. The resulting key will be
    of the same type (string or keyword) as the original.
  "

  ([kvs options]
     (assert (contains? options :via) "You must provide an index with `:via`.")

     (let [foreign-key-value (multi-get kvs (:via options))]
       (option-controlled-merge kvs
                                (index-select foreign-key-value options)
                                options)))

  ([kvs k v & rest] ; k and v are to give this different arity than above
     (extend-map kvs (apply hash-map k v rest))))


;;;;

(defn select-along-path
  "This is not really intended for public use. Note: doesn't handle compound keys."
  [val starting-index & foreign-index-pairs]
  (loop [many-return-values? false
         result [{::sentinel-key val}]
         [foreign-key next-index & remainder :as all]
           (concat [::sentinel-key starting-index] foreign-index-pairs)]

    (cond (empty? all)
          (if many-return-values? result (first result))

          (one-to-one-index? next-index)
          (recur many-return-values?
                 (set (map #(index-select (get % foreign-key) :using next-index) result))
                 remainder)

          :else
          (recur true
                 (set (mapcat #(index-select (get % foreign-key) :using next-index) result))
                 remainder))))

;; (defn combined-index-on [starting-index & foreign-index-pairs]
;;   (reduce-kv (fn [so-far v]
;;                (assoc so-far v (apply select-along-path v starting-index foreign-index-pairs)))
;;              {}
;;              (keys starting-index)))
