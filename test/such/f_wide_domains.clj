(ns such.f-wide-domains
  (:require [such.wide-domains :as subject])
  (:use midje.sweet))

(fact symbol
  (subject/symbol "th") => 'th
  (subject/symbol #'clojure.core/even?) => 'even?
  (subject/symbol *ns* "th2") => 'such.f-wide-domains/th2
  (subject/symbol 'such.f-wide-domains "th3") => 'such.f-wide-domains/th3
  (subject/symbol "such.f-wide-domains" "th4") => 'such.f-wide-domains/th4
  (subject/symbol "no.such.namespace" "th5") => 'no.such.namespace/th5
  (subject/symbol *ns* 'th6) => 'such.f-wide-domains/th6
  (subject/symbol *ns* :th7) => 'such.f-wide-domains/th7)
