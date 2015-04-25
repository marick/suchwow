(ns such.symbols
  (:require [such.casts :as cast]
            [clojure.string :as str]))

(defn from-concatenation [nameables]
  (symbol (str/join (map cast/as-name-string nameables))))

(defn without-namespace [sym]
  (symbol (name sym)))
