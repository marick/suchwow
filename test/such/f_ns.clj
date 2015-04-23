(ns such.f-ns  (:require [such.ns :as subject])
  (:use midje.sweet)
  (:use such.types))
            
(fact "a wow version of `find-ns`"
  (fact "original works on symbols but not its own result"
  (find-ns 'such.f-ns) => namespace?
  (find-ns (find-ns 'such.f-ns)) => (throws)))
  

