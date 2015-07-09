(ns such.f-wide-domains
  (:require [such.wide-domains :as subject]
            [clojure.set])
  (:use midje.sweet))


;; These tests are just copies of the ones in the originating namespace.
;; They give assurance that the particular functions actually *have* been
;; immigrated. There's no need to keep them in sync.

(fact +symbol
  (subject/+symbol "th") => 'th
  (subject/+symbol #'clojure.core/even?) => 'even?
  (subject/+symbol *ns* "th2") => 'such.f-wide-domains/th2
  (subject/+symbol 'such.f-wide-domains "th3") => 'such.f-wide-domains/th3
  (subject/+symbol "such.f-wide-domains" "th4") => 'such.f-wide-domains/th4
  (subject/+symbol "no.such.namespace" "th5") => 'no.such.namespace/th5
  (subject/+symbol *ns* 'th6) => 'such.f-wide-domains/th6
  (subject/+symbol *ns* :th7) => 'such.f-wide-domains/th7)


(def here-var)
(def intersection)

(fact +find-var
  (fact "old behavior still works"
    (subject/+find-var 'clojure.core/even?) => #'clojure.core/even?
    (subject/+find-var 'no-such-ns/even?) => (throws #"No such namespace")
    (subject/+find-var 'clojure.core/nonex) => nil)

  (fact "and there's new behavior in the one-argument case"
    (fact "lookup can be by symbol, string, or keyword"
      (subject/+find-var 'such.f-wide-domains/here-var) => #'here-var ; as before
      (subject/+find-var :such.f-wide-domains/here-var) => #'here-var
      (subject/+find-var "such.f-wide-domains/here-var") => #'here-var
      (subject/+find-var "no.such.namespace/here-var") => (throws #"No such namespace")
      (subject/+find-var "such.f-wide-domains/no-here") => nil)
      
    (fact "a symbol, string, or keyword without a namespace is looked up in `*ns*`"
      (subject/+find-var 'here-var) => #'such.f-wide-domains/here-var
      (subject/+find-var :here-var) => #'such.f-wide-domains/here-var
      (subject/+find-var "here-var") => #'such.f-wide-domains/here-var
      (subject/+find-var 'not-here) => nil)

    (fact "a var is just returned"
      (subject/+find-var #'even?) => #'clojure.core/even?))

  (fact "the two argument case is used for easier lookup"
    (fact "typical cases"
      (subject/+find-var 'clojure.core 'even?) => #'clojure.core/even?
      (subject/+find-var *ns* 'even?) => nil
      (subject/+find-var *ns* 'here-var) => #'here-var)

    (fact "other types of arguments"
      (subject/+find-var "clojure.core" "even?") => #'clojure.core/even?
      (subject/+find-var "clojure.core" #'even?) => #'clojure.core/even?
      (subject/+find-var *ns* #'intersection) => #'such.f-wide-domains/intersection
      (subject/+find-var *ns* :intersection) => #'such.f-wide-domains/intersection
      (subject/+find-var *ns* #'even?) => nil)

    (fact "namespace symbols can't have namespaces"
      (subject/+find-var 'derp/clojure.core 'odd?) => (throws)
      (subject/+find-var "derp/clojure.core" :odd?) => (throws))

    (fact "namespace parts of second argument are ignored - a bit icky"
      (subject/+find-var 'clojure.core 'derp/odd?) => #'clojure.core/odd?
      (subject/+find-var 'clojure.core "derp/odd?") => #'clojure.core/odd?
      (subject/+find-var "clojure.core" ::odd?) => #'clojure.core/odd?
      (subject/+find-var *ns* 'clojure.set/intersection) => #'intersection)))

(fact +into
  (let [result (subject/+into [] [1] (list 3 4))]
    result => [1 3 4]
    result => vector?))
