(ns such.f-ns  (:require [such.ns :as subject])
  (:use midje.sweet)
  (:require [such.vars :as var]))

(facts "about `with-scratch-namespace`"
  (fact "typical use"
    (subject/with-scratch-namespace scratch.ns
      (intern 'scratch.ns 'foo 3)
      (var/root-value (ns-resolve 'scratch.ns 'foo)) => 3)
    (find-ns 'scratch.ns) => nil)

  (fact "an existing namespace is deleted first"
    (create-ns 'scratch.ns)
    (intern 'scratch.ns 'foo 3)
    (ns-resolve 'scratch.ns 'foo) => var?

    (subject/with-scratch-namespace scratch.ns
      (ns-resolve 'scratch.ns 'foo) => nil ; deleted.
      (intern 'scratch.ns 'foo 3)
      (ns-resolve 'scratch.ns 'foo) => var?)
    (find-ns 'scratch.ns) => nil))
    
    
(def here-var)
(def intersection)

(fact find-var
  (fact "old behavior still works"
    (subject/find-var 'clojure.core/even?) => #'clojure.core/even?
    (subject/find-var 'no-such-ns/even?) => (throws #"No such namespace")
    (subject/find-var 'clojure.core/nonex) => nil)

  (fact "and there's new behavior in the one-argument case"
    (fact "lookup can be by symbol, string, or keyword"
      (subject/find-var 'such.f-ns/here-var) => #'here-var ; as before
      (subject/find-var :such.f-ns/here-var) => #'here-var
      (subject/find-var "such.f-ns/here-var") => #'here-var
      (subject/find-var "no.such.namespace/here-var") => (throws #"No such namespace")
      (subject/find-var "such.f-ns/no-here") => nil)
      
    (fact "a symbol, string, or keyword without a namespace is looked up in `*ns*`"
      (subject/find-var 'here-var) => #'such.f-ns/here-var
      (subject/find-var :here-var) => #'such.f-ns/here-var
      (subject/find-var "here-var") => #'such.f-ns/here-var
      (subject/find-var 'not-here) => nil)

    (fact "a var is just returned"
      (subject/find-var #'even?) => #'clojure.core/even?))

  (fact "the two argument case is used for easier lookup"
    (fact "typical cases"
      (subject/find-var 'clojure.core 'even?) => #'clojure.core/even?
      (subject/find-var *ns* 'even?) => nil
      (subject/find-var *ns* 'here-var) => #'here-var)

    (fact "other types of arguments"
      (subject/find-var "clojure.core" "even?") => #'clojure.core/even?
      (subject/find-var "clojure.core" #'even?) => #'clojure.core/even?
      (subject/find-var *ns* #'intersection) => #'such.f-ns/intersection
      (subject/find-var *ns* :intersection) => #'such.f-ns/intersection
      (subject/find-var *ns* #'even?) => nil)

    (fact "namespace symbols can't have namespaces"
      (subject/find-var 'derp/clojure.core 'odd?) => (throws)
      (subject/find-var "derp/clojure.core" :odd?) => (throws))

    (fact "namespace parts of second argument are ignored - a bit icky"
      (subject/find-var 'clojure.core 'derp/odd?) => #'clojure.core/odd?
      (subject/find-var 'clojure.core "derp/odd?") => #'clojure.core/odd?
      (subject/find-var "clojure.core" ::odd?) => #'clojure.core/odd?
      (subject/find-var *ns* 'clojure.set/intersection) => #'intersection)))


