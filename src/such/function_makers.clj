(ns such.function-makers
  "Functions that make other functions.
   
   Commonly used with a naming convention that flags such functions with
   `mkfn`:
   
       (ns ...
         (:require [such.function-makers :as mkfn]))
       ...
       (def stringlike? (mkfn/any-pred string? regex?))
")


(defn any-pred
  "Constructs a strict predicate that takes a single argument.
   That predicate returns `true` iff any of the `preds` is 
   truthy of that argument.
   
        (def stringlike? (mkfn:any-pred string? regex?))
        (stringlike? []) => false
        (stringlike? \"\") => true
   
        (def has-favs? (mkfn/any-pred (partial some #{0 4}) odd?)
        (has-favs? [2 4]) => true
        (has-favs? [1 6]) => true
   
   Stops checking after the first success. A predicate created from
   no arguments always returns `true`.

   Note: this predates [[some-fn]]. It differs in that it always returns
   `true` or `false`, and that it allows zero arguments (which produces a
   function that always returns `true`).
"
  [& preds]
  (if (empty? preds)
    (constantly true)
    (fn [arg]
      (loop [[candidate & remainder :as preds] preds]
        (cond (empty? preds)  false
              (candidate arg) true
              :else           (recur remainder))))))


(defn wrap-pred-with-catcher
  "Produces a new function. It returns whatever value `pred` does, except
   that it traps exceptions and returns `false`.

        ( (wrap-pred-with-catcher even?) 4) => true
        
        (even? :hi) => (throws)
        ( (wrap-pred-with-catcher even?) :hi) => false
"
  [pred]
  (fn [& xs]
    (try (apply pred xs)
    (catch Exception ex false))))

(defn mkfn:lazyseq
  "This is used to generate the `lazyseq:x->...` functions. See the source."
  [prefixer]
  (fn two-arg-form
    ([transformer pred]
       (fn lazily-handle [[x & xs :as lazyseq]]
         (lazy-seq 
          (cond (empty? lazyseq)
                nil
                          
                (pred x)
                ((prefixer x (transformer x)) (lazily-handle xs))
                          
                :else
                (cons x (lazily-handle xs))))))
    ([transformer]
       (two-arg-form transformer (constantly true)))))

(def ^{:arglists '([f] [f pred])}
  lazyseq:x->abc
  "Takes a transformer function and an optional predicate. 
   The transformer function must produce a collection, call it `coll`. 
   `pred` defaults to `(constantly true)`.
   Produces a function that converts one lazy sequence into another.
   For each element of the input sequence:
   
   * If `pred` is falsey, the unchanged element is in the output sequence.
   
   * If `pred` is truthy, the new `coll` is \"spliced\" into the output
     sequence in place of the original element.
    
           (let [replace-with-N-copies (lazyseq:x->abc #(repeat % %))]
             (replace-with-N-copies [0 1 2 3]) => [1 2 2 3 3 3])
           
           (let [replace-evens-with-N-copies (lazyseq:x->abc #(repeat % %) even?)]
             (replace-evens-with-N-copies [0 1 2 3]) => [1 2 2 3])
"
  (mkfn:lazyseq (fn [x tx] #(concat tx %))))

(def ^{:arglists '([f] [f pred])}
  lazyseq:x->xabc
  "The same behavior as [[lazyseq:x->abc]], except that the generated collection
   is spliced in *after* the original element, rather than replacing it.
    
           (let [augment-with-N-negatives (lazyseq:x->xabc #(repeat % (- %)))]
             (augment-with-N-negatives [0 1 2 3]) => [0 1 -1 2 -2 -2 3 -3 -3 -3])
"           
  (mkfn:lazyseq (fn [x tx] #(cons x (concat tx %)))))


(def ^{:arglists '([f] [f pred])}
  lazyseq:x->y
  "Takes an arbitrary function and an optional predicate. 
   `pred` defaults to `(constantly true)`.
   Produces a function that converts one lazy sequence into another.
   It differs from the input sequence in that elements for which `pred`
   is truthy are replaced with `(f elt)`.
    
           (let [force-positive (lazyseq:x->y - neg?)]
             (force-positive [0 1 -2 -3]) => [0 1 2 3])
"
  (mkfn:lazyseq (fn [x tx] #(cons tx %))))
            


            
(defn lazyseq:criticize-deviationism
  "Produces a function that inspects a given `coll` according to the `deviancy-detector`.
   When a deviant element is found, the `reporter` is called for side-effect. It is given
   the `coll` and the deviant element as arguments.
   
   All deviant elements are reported. The original collection is returned.
    
          (def bad-attitude? neg?)
          (def flagged-negativity 
               (lazyseq:criticize-deviationism (comp neg? second)
                                               (fn [coll elt]
                                                 (println \"Bad attitude from\" elt))))
          
          (def attitudes [ [:fred 32] [:joe 23] [:gary -10] [:brian -30] [:corey 10326] ])
          (flagged-negativity attitudes) ;; :gary and :brian are flagged.
"
  [deviancy-detector reporter]
  (fn [coll]
    (doseq [x coll]
      (when (deviancy-detector x) (reporter coll x)))
    coll))
