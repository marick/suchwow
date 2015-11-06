(ns such.random
  "Random numbers and crypographic hashes"
  (:import org.apache.commons.codec.digest.DigestUtils))


(defn guid
  "A random almost-certainly-unique identifier"
  []
  (str (java.util.UUID/randomUUID)))

(def uuid "Synonym for `guid`" guid)

(defn form-hash
  "Returns a SHA-1 hash (encoded as a hex string) from the `prn` representation of the input.
   Use for collision avoidance when the highest security is not needed."
  [form]
  ;; Note: this is deprecated in later versions of commons.codec. However,
  ;; we're using an old version for compatibility with Compojure.
  ;; When compojure updates, we can use sha1Hex.
  (DigestUtils/shaHex (pr-str form)))
