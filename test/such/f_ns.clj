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
    
    
