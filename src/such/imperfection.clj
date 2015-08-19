(ns such.imperfection
  "Were we perfect, we wouldn't need to test or debug. Since we're not, a
   few helpers, especially for code written in flow style."
  (:require [clojure.pprint :refer [pprint]]
            [such.readable :as readable]))

(defmacro val-and-output
  "Execute the body. Instead of just returning the resulting value,
   return a pair of the `value` and any output (as with `with-out-str`)."
  [& body]
  `(let [val-recorder# (atom nil)
         str-recorder# (with-out-str (swap! val-recorder# (fn [_#] (do ~@body))))]
     (vector (deref val-recorder#) str-recorder#)))

(defn -pprint-
  "Unlike regular `pprint`, this returns the value passed in, making it useful
   for cases like this:
   
        (-> v
            frob
            -pprint-
            tweak
            -pprint-)

  [[value]] is used to produce more helpful function-names.
"
  [v]
  (pprint (readable/value v))
  v)

(defn -prn-
  "Unlike regular `prn`, this returns the value passed in, making it useful
   for cases like this:
   
        (-> v
            frob
            -prn-
            tweak
            -prn-)

  [[value]] is used to produce more helpful function-names.
"
  [v]
  (prn (readable/value v))
  v)

(defn tag-
  "Prints (as with `println`) the given `string`.
   The `value` is returned. Use as follows:

        (->> v
             frob
             (tag \"frobout\") -prn-
             ...)
"
  [string value]
  (println string)
  value)
  
(defn -tag
  "Formats the `string` and `args` and prints the results as with `println`.
   The `value` is returned. Use as follows:

        (-> v
             frob
             (tag \"Frob with %s\" flag) (-pprint-)
             ...)
"
  [value string & args]
  (println (apply format string args))
  value)
  
  
