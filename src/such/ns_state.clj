(ns such.ns-state
  "Manipulate, in a stateful way, a key-value store that belongs to a particular namespace.
   
   Throughout this documentation, `f` is any callable. It must not have side effects.
   `k` is a value (of any type) that indexes the key-value store.

   Values in the store can be treated as single entities or as stacks.

   All state-changing operations end in `\"!\". They are atomic.
   Except where explicitly noted, their return value is undefined."
  (:require [such.wrongness :as !])
  (:refer-clojure :exclude [dissoc! pop!] :rename {get core-get
                                                   count core-count
                                                   empty? core-empty?}))

(defn- state []
  (-> *ns* meta ::state))

(defn alter!
  "Replace the value of the store at `k` with the value
   of `(apply f <current-val> args)`.
   
       (nss/alter! :counter + 2)
"
  [k f & args]
  (alter-meta! *ns* #(apply update-in % [::state k] f args))
  :undefined)

(defn set!
  "Replace the value of the store at `k`
   with `v`.
   
       (nss/set! :counter 0)
"
  [k v]
  (alter! k (constantly v)))

(defn dissoc!
  "In the no-argument versions, delete all keys from the store.
   In the N-argument version, delete each of the keys."
  ([& ks]
     (alter-meta! *ns* (fn [all] (update-in all [::state] #(apply dissoc % ks))))
     :undefined)
  ([]
     (alter-meta! *ns* dissoc ::state)
     :undefined))

(defn get
  "Return the value of the store at `k` or the default if there is no value.
   If no default value, return `nil`."
  ([k]
     (get k nil))
  ([k default]
     (get-in (meta *ns*) [::state k] default)))


;; Stack-structured

(defn count
  "The value of the store at `k` must be a stack. Returns the number of elements.
   A never-created (or destroyed) stack has zero elements."
  [k]
  (core-count (get k)))

(defn empty?
  "The value of the store at `k` must be a stack. Returns `true` iff the stack
   has no elements. A never-created (or destroyed) stack has zero elements."
  [k]
  (core-empty? (get k)))

(defn top
  "Returns the element of the stack at `k` that was most recently pushed.
   Throws an `Exception` if the stack is empty or does not exist.
   
        (nss/push! :s 1)
        (nss/push! :s 2)
        (nss/pop! :s)
        (nss/top :s) => 1
"         
  [k]
  (if (empty? k)
    (!/boom! "Namespace state `%s` is empty." k)
    (peek (get k))))

(defn push!
  "Change the stack at `k` to have `v` as its [[top]] element.
  The stack need not be created before the first push."
  [k v]
  (alter! k #(conj (or % []) v)))

(defn pop!
  "Change the stack at `k` to remove its [[top]].
   Throws an `Exception` if the stack is empty or does not exist."
  [k]
  (let [result (top k)]
    (alter! k pop)
    result))

(defn history
  "The value of the store at `k` must be a stack. The elements are returned in the
   order they were pushed. (Thus, [[top]] is the final element.) The return value
   is specifically a vector.
   
        (nss/push! :s 1)
        (nss/push! :s 2)
        (nss/history :s) => [1 2]
"
  [k]
  (vec (get k)))

(defn flattened-history 
  "The value of the store at `k` must be a stack. Each element must be a sequential
   collection. The result is a vector of `flatten` applied to the [[history]].

        (nss/push! :s [1 2)
        (nss/push! :s [])
        (nss/push! :s [3])
        (nss/flattened-history :s) => [1 2 3]
"
  [k]
  (vec (flatten (history k))))
