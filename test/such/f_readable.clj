(ns such.f-readable
  (:require [such.readable :as subject]
            [com.rpl.specter :as specter])
  (:use midje.sweet))

(defmulti multi identity)
(subject/set-function-elaborations! subject/default-function-elaborations)

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
    (let [f (subject/rename (fn []) 'fred)]
      (subject/fn-symbol f) => '<fred>))

  (fact "effect of elaborations"
    (subject/with-function-elaborations {:anonymous-name "functoid" :surroundings ""}
      (subject/fn-symbol (fn [])) => 'functoid
      (subject/fn-symbol even?) => 'even?)))

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

 (fact value-string
   (fact "plain output"
     (subject/value 1) => 1
     (subject/value-string 1) => "1")

   (fact "given translation"
     (subject/with-translations [5 :five]
       (subject/value 5) => :five
       (subject/value-string 5) => ":five")
     (subject/value-string 5) => "5")

   (fact "a plain function"
     (subject/value even?) => '<even?>
     (subject/value-string even?) => "<even?>")

   (fact "a translation takes precedence over the automatic name"
     (subject/with-translations [even? 'EVEN!]
       (subject/value even?) => 'EVEN!
       (subject/value-string even?) => "EVEN!"))

   (fact "flat lists of functions"
     (let [foo (fn [a] 1)
           bar (fn [a] 2)]
       (subject/value [(fn []) (fn []) foo bar foo even?])
       =>             '[<fn> <fn-2> <foo> <bar> <foo> <even?>]
       (subject/value-string [(fn []) (fn []) foo bar foo even?])
       =>             "[<fn> <fn-2> <foo> <bar> <foo> <even?>]"))

   (fact "embedded translations"
     (subject/with-translations [5 'five]
       (subject/value [3 4 5 6]) => [3 4 'five 6]
       (subject/value-string [3 4 5 6]) => "[3 4 five 6]"))

   (fact "you can translate complex structures too"
     (subject/with-translations [[1 2 3] 'short]
       (subject/value [[1 2 3] [1 2] [1 2 3 4]]) => ['short [1 2] [1 2 3 4]]
       (subject/value-string [[1 2 3] [1 2] [1 2 3 4]]) => "[short [1 2] [1 2 3 4]]"))

   (fact "translations take precedence over automatic function names"
     (subject/with-translations [even? :even]
       (subject/value-string [even? odd?]) => "[:even <odd?>]"))

   (fact "nested functions"
     (let [foo (fn [a] 1)
           named (subject/rename (fn [b] 2) "named")]
       (subject/value-string [(fn [])
                       foo
                       named
                       [(fn []) foo]
                       [[[foo]]]])
       => "[<fn> <foo> <named> [<fn-2> <foo>] [[[<foo>]]]]"))

   (fact "generated functions have indexes repeated"
     (let [generator (fn [x] (fn [y] (+ x y)))
           one (generator 1)
           two (generator 2)]
       (subject/value-string one) => "<fn>"
       (subject/value-string [one two one two]) => "[<fn> <fn-2> <fn> <fn-2>]"))

   (fact "changing elaborations"
     ;; Since the `with` form is used above
     (let [generator (fn [x] (fn [y] (+ x y)))
           one (generator 1)
           two (generator 2)]
       (subject/set-function-elaborations! {:anonymous-name "derp" :surroundings "{{}}"})
       (subject/value-string [ [ [generator one two two one generator]]])
       => "[[[{{generator}} {{derp}} {{derp-2}} {{derp-2}} {{derp}} {{generator}}]]]"
       (subject/set-function-elaborations! subject/default-function-elaborations)
       (subject/value-string [ [ [generator one two two one generator]]])
       => "[[[<generator> <fn> <fn-2> <fn-2> <fn> <generator>]]]")))
      


(fact "global transation functions"
  (subject/forget-translations!)
  (subject/value-string 5) => "5"
  (subject/instead-of 5 'five)
  (subject/value-string 5) => "five"
  (subject/forget-translations!)
  (subject/value-string 5) => "5")


(fact "a combo : translations take precedence"
  (subject/with-translations [5 :five
                              specter/ALL 'ALL
                              even? odd?]
    (subject/value-string {even? odd?,
                           :deep [(fn []) 3 5 [specter/ALL + specter/ALL]]})
    => "{<odd?> <odd?>, :deep [<fn> 3 :five [ALL <+> ALL]]}"))
