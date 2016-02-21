(ns such.imperfection
  "Were we perfect, we wouldn't need to test or debug. Since we're not, a
   few helpers organized around printing."
  (:require [clojure.pprint :refer [pprint]]
            [such.readable :as readable]
            [such.metadata :as meta]
            [such.ns :as ns]))

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
  "Prints (as with `println`) the given `tag`, which may be any value.
   The `value` is returned. 

        (->> v
             frob
             (tag \"frobout\") -prn-
             ...)
"
  [tag value]
  (println tag)
  value)
  
(defn -tag
  "If `tag` is a string, formats `tag` and `args` and prints the results as with `println`.
   If `tag` is not a string, it is printed and any `args` are ignored.
   The `value` is returned. 

   Use as follows:

        (-> v
             frob
             (tag \"Frob with %s\" flag) (-pprint-)
             quux
             (tag :quux)
             ...)
"
  [value tag & args]
  (println (if (string? tag)
             (apply format tag args)
             tag))
  value)


(defn- one-e [[existing-sym prefix suffix]]
  (let [outsym '*out*
        errsym '*err*
        args 'args
        new-sym (symbol (str "e" existing-sym))
        docstring (format "Like %s, but prints to `*err*`.
  Useful for keeping debug output from being captured by [[val-and-output]]."
                          (str prefix existing-sym suffix))]
    `(defn ~new-sym ~docstring [& ~args]
       (binding [~outsym ~errsym]
         (apply ~existing-sym ~args)))))

(defmacro e
  {:private true}
  [& pairs]
  `(do ~@(map one-e pairs)))

(e [pr "`" "`"]
   [prn "`" "`"]
   [print "`" "`"]
   [println "`" "`"]
   [pprint "`" "`"]
   [-pprint- "[[" "]]"]
   [-prn- "[[" "]]"]
   [tag- "[[" "]]"]
   [-tag "[[" "]]"])
