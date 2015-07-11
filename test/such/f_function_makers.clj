(ns such.f-function-makers
  (:require [such.function-makers :as mkfn])
  (:use midje.sweet))


(fact "pred:any?"
  ((mkfn/pred:any? odd? even?) 1) => true
  ((mkfn/pred:any? pos? neg?) 0) => false
  ((mkfn/pred:any? :key :word) {:key false}) => false
  ((mkfn/pred:any? :key :word) {:key false :word 3}) => true
  ((mkfn/pred:any? #{1 2} #{3 4}) 3) => true
  ;; stops at first match
  ((mkfn/pred:any? (partial = 3) (fn[_](throw (new Error "boom!")))) 3) => true
  ;; Any empty list means that everything matches
  ((mkfn/pred:any?) 3) => true)

(fact pred:exception->false
  (let [wrapped (mkfn/pred:exception->false even?)]
    (wrapped 2) => true
    (wrapped 3) => false
    (even? nil) => (throws)
    (wrapped nil) => false))
  

(fact "`lazyseq:x->abc` converts (possibly optionally) each element of a lazyseq and replaces it with N results"
  (fact "one arg form processes each element"
    ( (mkfn/lazyseq:x->abc #(repeat % %)) [1 2 3]) => [1 2 2 3 3 3])

  (fact "two arg form processes only elements that match predicate"
    ( (mkfn/lazyseq:x->abc #(repeat % %) even?) [1 2 3 4]) => [1 2 2 3 4 4 4 4])

  (fact "empty sequences are handled"
    ( (mkfn/lazyseq:x->abc #(repeat % %) even?) []) => empty?)

  (fact "it is indeed lazy"
    (let [made (mkfn/lazyseq:x->abc #(repeat % %) even?)]
      (take 2 (made [0])) => empty?
      (take 2 (made [0 1])) => [1]
      (take 2 (made [0 1 2])) => [1 2]
      (take 2 (made [0 1 2 3])) => [1 2]
      (take 2 (made [0 1 2 3 4])) => [1 2]
      (take 2 (made (range))) => [ 1 2 ]
      (count (take 100000 (made (range)))) => 100000)))


(fact "`lazyseq:x->xabc` converts (possibly optionally) each element of a lazyseq and replaces it
       with N results. The first argument is preserved"
  (fact "one arg form processes each element"
    ( (mkfn/lazyseq:x->xabc #(repeat % (- %))) [1 2 3]) => [1 -1 2 -2 -2 3 -3 -3 -3])

  (fact "two arg form processes only elements that match predicate"
    ( (mkfn/lazyseq:x->xabc #(repeat % (- %)) even?) [1 2 3 4]) => [1 2 -2 -2 3 4 -4 -4 -4 -4]))


(fact "`lazyseq:x->y` converts (possibly optionally) each element of a lazyseq and replaces it
       with 1 result."
  (fact "one arg form processes each element"
    ( (mkfn/lazyseq:x->y -) [1 2 3]) => [-1 -2 -3 ])

  (fact "two arg form processes only elements that match predicate"
    ( (mkfn/lazyseq:x->y - even?) [1 2 3 4]) => [1 -2 3 -4]))


(fact lazyseq:criticize-deviationism
  (let [recorder (atom [])
        messager #(format "%s - %s" %1 %2)
        critiquer (mkfn/lazyseq:criticize-deviationism (comp neg? second)
                                                       #(swap! recorder
                                                               conj
                                                               (messager %1 %2)))
        data [[:ok 0] [:brian -1] [:corey 10326] [:gary -3]]]
    (critiquer data) => data
    @recorder => [(format "%s - %s" data [:brian -1])
                  (format "%s - %s" data [:gary -3])]))
                                                       
