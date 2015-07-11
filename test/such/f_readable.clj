(ns such.f-readable
  (:require [such.readable :as subject])
  (:use midje.sweet)
  (:use [such.versions :only [when>=1-7]]))

(defmulti multi identity)

(fact fn-symbol
  (fact "plain functions"
    (subject/fn-symbol even?) => '<even?>
    (subject/fn-symbol (fn [])) => '<fn>
    
    (let [f ( ( (fn [a] (fn [b] (fn [c] (+ a b c)))) 1) 2)]
      (subject/fn-symbol f) => '<fn>)
    
    (let [f ( ( (fn [a] (fn [b] (fn my:tweedle-dum [c] (+ a b c)))) 1) 2)]
      (subject/fn-symbol f) => '<my:tweedle-dum>))

  (fact "multimethods"
    (subject/fn-symbol multi) => '<fn>)

  (fact letfn
    (letfn [(x [a] a)]
      (subject/fn-symbol x) => '<x>))

  (fact "behavior within let"
    (let [foo (fn [] 1)]
      (subject/fn-symbol foo) => '<foo>))

  (fact "function-generating functions"
    (let [gen (fn [x] (fn [y] (+ x y)))]
      (subject/fn-symbol (gen 3)) => '<fn>))

  (fact "readable name given"
    (let [f (subject/with-name (fn []) 'fred)]
      (subject/fn-symbol f) => '<fred>)))

(fact fn-string
  (fact "plain functions"
    (subject/fn-string even?) => "<even?>"
    (subject/fn-string (fn [])) => "<fn>"
    
    (let [f ( ( (fn [a] (fn [b] (fn [c] (+ a b c)))) 1) 2)]
      (subject/fn-string f) => "<fn>")
    
    (let [f ( ( (fn [a] (fn [b] (fn my:tweedle-dum [c] (+ a b c)))) 1) 2)]
      (subject/fn-string f) => "<my:tweedle-dum>"))

  (fact "multimethods"
    (subject/fn-string multi) => "<fn>")

  (fact letfn
    (letfn [(x [a] a)]
      (subject/fn-string x) => "<x>")))

 (future-fact "function names can be provided with a metadata value (show-as)")

    

(when>=1-7

 (fact value
   (fact "plain output"
     (subject/value 1) => "1")

   (fact "a plain function"
     (subject/value even?) => "<even?>")

   (fact "flat lists of functions"
     (let [foo (fn [a] 1)
           bar (fn [a] 2)]
       (subject/value [(fn []) (fn []) foo bar foo even?])
       =>             "[<fn> <fn-2> <foo> <bar> <foo> <even?>]"))

   (fact "nested functions"
     (let [foo (fn [a] 1)
           named (subject/with-name (fn [b] 2) "named")]
       (subject/value [(fn [])
                       foo
                       named
                       [(fn []) foo]
                       [[[foo]]]])
       => "[<fn> <foo> <named> [<fn-2> <foo>] [[[<foo>]]]]"))

   (fact "generated functions have indexes repeated"
     (let [generator (fn [x] (fn [y] (+ x y)))
           one (generator 1)
           two (generator 2)]
       (subject/value one) => "<fn>"
       (subject/value [one two one two]) => "[<fn> <fn-2> <fn> <fn-2>]")))


(fact "translations"
  (subject/forget-translations!)
  (subject/instead-of 5 'five)
  (subject/value [3 4 5 6]) => "[3 4 five 6]")







)



