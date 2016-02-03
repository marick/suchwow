(ns such.f-control-flow
  (:require [such.control-flow :as subject]
            [midje.sweet :refer :all]))

(facts "about let-maybe"
  (fact "often the same as `let`"
    (subject/let-maybe [] 3) => 3
    (subject/let-maybe [a 1] a) => 1
    (subject/let-maybe [a 1
                        b (inc a)]
      (+ a b)) => 3)

  (fact "nil stops processing"
    (subject/let-maybe [a nil
                        b (throw "Exception")]
      (throw "exception")) => nil)

  (fact "false does not"
    (subject/let-maybe [a false
                        b true]
       (or a b)) => true))
