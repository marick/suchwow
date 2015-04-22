(ns such.f-ns  (:require [such.ns :as subject])
  (:use midje.sweet)
  (:use such.types))
            

(fact "find-ns"
  (find-ns 'such.f-ns) => namespace?)
