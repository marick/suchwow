(ns such.readable
  (:use [such.versions :only [when>=1-7]])
  (:refer-clojure :exclude [print])
  (:require [such.symbols :as symbol]
            [such.types :as type]
            [clojure.string :as str]
            [clojure.repl :as repl]))

(defn- generate-name [f base-name previously-seen]
  (if (contains? @previously-seen f)
    (@previously-seen f)
    (let [name (if (empty? @previously-seen)
                 base-name
                 (str base-name "-" (+ 1 (count @previously-seen))))]
      (swap! previously-seen assoc f name)
      name)))

(defn- readable-name [f]
  (-> (str f)
      repl/demunge
      (str/split #"/")
      last
      (str/split #"@")
      first
      (str/split #"--[0-9]+$")
      first
      ;; last clause required by 1.5.X
      (str/replace "-COLON-" ":")))

(def ^:private anonymous? #{"fn" "clojure.lang.MultiFn"})

(defn elaborate-fn-symbol
  [f base-name surroundings previously-seen]
  (let [candidate (readable-name f)]
    (symbol/from-concatenation [(.substring surroundings 0 (/ (count surroundings) 2))
                                (if (anonymous? candidate)
                                  (generate-name f base-name previously-seen)
                                  candidate)
                                (.substring surroundings (/ (count surroundings) 2))])))


(def default-function-string "fn")
(def default-surroundings "<>")


(defn fn-symbol
  [f]
  (elaborate-fn-symbol f default-function-string default-surroundings (atom {})))

(defn fn-string
  [f]
  (str (fn-symbol f)))
  

(when>=1-7

(require '[com.rpl.specter :as specter])


(def ^:private translations (atom {}))

(defn forget-translations! []
  (reset! translations {}))

(defn instead-of [value show]
  (swap! translations assoc value show))


(defn- better-aliases [x aliases]
  (specter/transform (specter/walker #(contains? @aliases %))
                     @aliases
                     x))


(defn- better-function-names [x previously-seen]
  (specter/transform (specter/walker type/extended-fn?)
                     #(elaborate-fn-symbol % default-function-string
                                           default-surroundings
                                           previously-seen)
                     x))

(defn value [x]
  (pr-str
   (cond (type/extended-fn? x)
         (fn-symbol x)
         
         (coll? x)
         (let [previously-seen (atom {})]
           (-> x 
               (better-function-names previously-seen)
               (better-aliases translations)
               ))
         
         :else 
         x)))

          



)

