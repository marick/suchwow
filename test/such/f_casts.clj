(ns such.f-casts (:require [such.casts :as cast])
  (:use midje.sweet))

(fact has-namespace?
  (cast/has-namespace? 1) => (throws)
  (cast/has-namespace? 'some.namespace) => false
  (cast/has-namespace? 'some.namespace/some.namespace) => true
  (cast/has-namespace? :food) => false
  (cast/has-namespace? :clojure.core) => false
  (cast/has-namespace? :clojure.core/food) => true
  (cast/has-namespace? "foo") => false
  (cast/has-namespace? "clojure.core/foo") => true
  (cast/has-namespace? "clojure.core/foo/bar") => (throws))

(fact as-ns-symbol
  (cast/as-ns-symbol *ns*) => 'such.f-casts
  (cast/as-ns-symbol 'some.namespace) => 'some.namespace
  (cast/as-ns-symbol 'some.namespace/some.namespace) => (throws)
  (cast/as-ns-symbol :food) => 'food
  (cast/as-ns-symbol :clojure.core) => 'clojure.core
  (cast/as-ns-symbol :clojure.core/food) => (throws)
  (cast/as-ns-symbol "foo") => 'foo
  (cast/as-ns-symbol "clojure.core/foo") => (throws))

(fact extract-namespace-into-symbol
  (cast/extract-namespace-into-symbol *ns*) => 'such.f-casts
  (cast/extract-namespace-into-symbol 'some.namespace) => (throws)
  (cast/extract-namespace-into-symbol 'some.namespace/x) => 'some.namespace
  (cast/extract-namespace-into-symbol :food) => (throws)
  (cast/extract-namespace-into-symbol :clojure.core) => (throws)
  (cast/extract-namespace-into-symbol :clojure.core/food) => 'clojure.core
  (cast/extract-namespace-into-symbol "foo") => (throws)
  (cast/extract-namespace-into-symbol "clojure.core/foo") => 'clojure.core)



(def local)

(fact "as-symbol-without-namespace"
  (cast/as-symbol-without-namespace 'foo) => 'foo
  (cast/as-symbol-without-namespace 'clojure.core/foo) => 'foo
  (cast/as-symbol-without-namespace :clojure.core/even?) => 'even?
  (cast/as-symbol-without-namespace ::even?) => 'even?
  (cast/as-symbol-without-namespace :even?) => 'even?
  (cast/as-symbol-without-namespace #'clojure.core/even?) => 'even?
  (cast/as-symbol-without-namespace #'local) => 'local
  (cast/as-symbol-without-namespace "local") => 'local
  (cast/as-symbol-without-namespace "core.foo/bar") => 'bar)

  

(fact "as-string-without-namespace"
  (cast/as-string-without-namespace 'clojure/foo) => "foo"   ; namespace omitted
  (cast/as-string-without-namespace #'even?) => "even?"
  (cast/as-string-without-namespace :bar) => "bar"           ; colon omitted.
  (cast/as-string-without-namespace :util.x/quux) => "quux"  ; \"namespace\" omitted
  (cast/as-string-without-namespace "derp") => "derp")

(def here)
(fact as-namespace-and-name-symbols
  (cast/as-namespace-and-name-symbols 'clojure.core/even?) => ['clojure.core 'even?]
  (cast/as-namespace-and-name-symbols 'even?) => [nil 'even?]
  
  (cast/as-namespace-and-name-symbols :clojure.core/even?) => ['clojure.core 'even?]
  (cast/as-namespace-and-name-symbols :foo) => [nil 'foo]
  
  (cast/as-namespace-and-name-symbols "even?") => [nil 'even?]
  (cast/as-namespace-and-name-symbols "clojure.core/even?") => ['clojure.core 'even?]
  (cast/as-namespace-and-name-symbols "clojure/core/even?") => (throws)
  
  (cast/as-namespace-and-name-symbols #'even?) => ['clojure.core 'even?]
  (cast/as-namespace-and-name-symbols #'here) => ['such.f-casts 'here])
