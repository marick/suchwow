(ns such.f-ns-state (:require [such.ns-state :as subject])
    (:use midje.sweet))

(subject/dissoc!)

(fact "state starts out as empty"
  (#'subject/state) => empty?
  (subject/get :k) => empty?)

(fact "setting"
  (subject/alter! :setting (constantly 1))
  (subject/get :setting) => 1

  (subject/alter! :setting + 3)
  (subject/get :setting) => 4

  (subject/set! :setting 0)
  (subject/get :setting) => 0

  (subject/get :missing) => nil
  (subject/get :missing :nowhere) => :nowhere)

(fact "stack interface"
  (subject/count :stack) => 0
  (subject/empty? :stack) => true
  (subject/pop! :stack) => (throws)
  
  (subject/push! :stack "top")
  (subject/get :stack) => ["top"]

  (subject/top :stack) => "top"
  (subject/count :stack) => 1
  (subject/empty? :stack) => false

  (subject/pop! :stack) => "top"

  (subject/top :stack) => (throws)
  (subject/count :stack) => 0
  (subject/empty? :stack) => true
  (subject/get :stack) => empty?)

(fact "note that stacks can contain nils"
  (subject/push! :nilable nil)
  (subject/push! :nilable 3)

  (subject/pop! :nilable) => 3
  (subject/pop! :nilable) => nil
  (subject/pop! :nilable) => (throws))

(fact "stack history"
  (subject/history :history) => []
  (subject/history :history) => vector?
  
  (subject/push! :history 1)
  (subject/push! :history 2)
  (subject/history :history) => [1 2]
  (subject/history :history) => vector?

  (fact "history requires a that the value of a key be something that can be made a vector"
    (subject/set! :history 1)
    (subject/history :history) => (throws)))


(fact "flattened history"
  (subject/push! :flattened [1 2])
  (subject/push! :flattened [3])
  (subject/flattened-history :flattened) => [1 2 3]

  (fact "history isn't lazy"
    (subject/flattened-history :flattened) => vector?))

(fact "the key needn't be a keyword"
  (subject/get 'symbol) => nil
  (subject/set! 'symbol [])
  (subject/get 'symbol) => []
  (subject/top 'symbol) => (throws)
  (subject/pop! 'symbol) => (throws)
  (subject/push! 'symbol [1])
  (subject/get 'symbol) => [[1]]
  (subject/push! 'symbol [2])
  (subject/get 'symbol) => [[1] [2]]
  (subject/history 'symbol) => [[1] [2]]
  (subject/flattened-history 'symbol) => [1 2])

(fact "cleanup"
  (subject/get :setting) =not=> nil
  (subject/dissoc! :setting) 
  (subject/get :setting :missing) => :missing

  (#'subject/state) =not=> nil
  (subject/get :flattened) =not=> nil
  (subject/dissoc!)
  (#'subject/state) => nil
  (subject/get :flattened :missing) => :missing)

