(ns such.f-relational
  (:require [such.versions :refer [when>=1-7 when>=1-6]]
            [such.relational :as subject]
            [such.metadata :as meta]
            [clojure.set :as set]
            [clojure.pprint :refer [pprint]]
            [midje.sweet :refer :all]))

(when>=1-7


(fact "indices"
  (fact "one-to-one indices"
    (let [data [{:id 1 :rest ..rest1..} {:id 2 :rest ..rest2..}]
          index (subject/one-to-one-index-on data :id)]
      (subject/index-select 1 :using index) => {:id 1 :rest ..rest1..}))

  (fact "one-to-one indices where the keys are compound"
    (let [data [{:id 1 :pk 1 :rest ..rest11..}
                {:id 1 :pk 2 :rest ..rest12..}
                {:id 2 :pk 2 :rest ..rest22..}]
          index (subject/one-to-one-index-on data [:id :pk])]
      (subject/index-select [1 1] :using index) => (first data)
      (subject/index-select [1 2] :using index) => (second data)))

  (fact "one-to-many indices"
    (let [data [{:id 1 :rest ..rest11..}
                {:id 1 :rest ..rest12..}
                {:id 2 :rest ..rest22..}]
          index (subject/one-to-many-index-on data :id)]
      (subject/index-select 1 :using index) => (just (first data) (second data) :in-any-order)))

  (fact "selecting along a path"
    (fact "one-to-one-to-one"
      (let [top [{:id 1 :foreign "a"} {:id 2 :foreign "b"}]
            alpha [{:id "a" :alfor "1a"} {:id "b" :alfor "2b"}]
            bottom [{:id "1a" :val "1aval"} {:id "2b" :val "2bval"}]
            top-index (subject/one-to-one-index-on top :id)
            alpha-index (subject/one-to-one-index-on alpha :id)
            bottom-index (subject/one-to-one-index-on bottom :id)]
        (fact "one extra level"
          (subject/select-along-path 1 top-index :foreign alpha-index)
          => (just (first alpha)))

        (fact "two extra levels"
          (subject/select-along-path 1 top-index :foreign alpha-index :alfor bottom-index)
          => (just (first bottom)))))


    (fact "variations on one-to-many and one-to-one"
      (let [one-to-one-top [{:id "top" :foreign "middle"}]
            one-to-one-middle [{:id "middle" :foreign "bottom"}
                               {:id "middle2" :foreign "bottom2"}]
            one-to-one-bottom [{:id "bottom"}
                               {:id "bottom2"}]

            one-to-many-top [{:id "top" :foreign "middle"}
                             {:id "top" :foreign "middle2"}]
            one-to-many-middle [{:id "middle" :foreign "bottom"}
                                {:id "middle" :foreign "bottom2"}
                                {:id "middle2" :foreign "bottom"}
                                {:id "middle2" :foreign "bottom2"}]
            one-to-many-bottom [{:id "bottom" :tag 1}
                                {:id "bottom" :tag 2}
                                {:id "bottom2" :tag 3}
                                {:id "bottom2" :tag 4}]

            one-to-one-top-index (subject/one-to-one-index-on one-to-one-top :id)
            one-to-one-middle-index (subject/one-to-one-index-on one-to-one-middle :id)
            one-to-one-bottom-index (subject/one-to-one-index-on one-to-one-bottom :id)

            one-to-many-top-index (subject/one-to-many-index-on one-to-many-top :id)
            one-to-many-middle-index (subject/one-to-many-index-on one-to-many-middle :id)
            one-to-many-bottom-index (subject/one-to-many-index-on one-to-many-bottom :id)]


        (fact "1-N 1-N 1-N"
          (subject/select-along-path "top" one-to-many-top-index
                                     :foreign one-to-many-middle-index
                                     :foreign one-to-many-bottom-index)
          => (just {:id "bottom" :tag 1} {:id "bottom" :tag 2}
                   {:id "bottom2" :tag 3} {:id "bottom2" :tag 4} :in-any-order))


        (fact "1-N 1-1 1-N"
          (subject/select-along-path "top" one-to-many-top-index
                                     :foreign one-to-one-middle-index
                                     :foreign one-to-many-bottom-index)
          => (just {:id "bottom" :tag 1} {:id "bottom" :tag 2}
                   {:id "bottom2" :tag 3} {:id "bottom2" :tag 4} :in-any-order))


        (fact "1-N 1-1 1-1"
          (subject/select-along-path "top" one-to-many-top-index
                                     :foreign one-to-one-middle-index
                                     :foreign one-to-one-bottom-index)
          => (just {:id "bottom"} {:id "bottom2"} :in-any-order))

        (fact "1-1 1-N 1-1"
          (subject/select-along-path "top" one-to-one-top-index
                                     :foreign one-to-many-middle-index
                                     :foreign one-to-one-bottom-index)
          => (just {:id "bottom"} {:id "bottom2"} :in-any-order))

        (fact "1-1 1-1 1-N"
          (subject/select-along-path "top" one-to-one-top-index
                                     :foreign one-to-one-middle-index
                                     :foreign one-to-many-bottom-index)
          => (just {:id "bottom" :tag 1} {:id "bottom" :tag 2} :in-any-order))


        (fact "1-1 1-1 1-1"
          (subject/select-along-path "top" one-to-one-top-index
                                     :foreign one-to-one-middle-index
                                     :foreign one-to-one-bottom-index)
          => {:id "bottom"})))))



  (fact "options"
    (let [data [{:id 1 :rest ..rest1..} {:id 2 :rest ..rest2..}]
          index (subject/one-to-one-index-on data :id)]
      (fact "can limit the number of keys returned"
        (subject/index-select 1 :using index :keys [:rest]) => {:rest ..rest1..})
      (fact "can add a prefix to keys as keyword..."
        (subject/index-select 1 :using index :prefix :pre-) => {:pre-id 1 :pre-rest ..rest1..})
      (fact "both"
        (subject/index-select 1 :using index :keys [:rest] :prefix :pre-) => {:pre-rest ..rest1..})

      (fact "options can also be provided as maps"
        (subject/index-select 1 {:using index :keys [:rest]}) => {:rest ..rest1..})))

  (fact "options for one-to-many-maps"
    (let [data [{:id 1 "rest" ..rest11..}
                {:id 1 "rest" ..rest12..}
                {:id 2 "rest" ..rest22..}]
          index (subject/one-to-many-index-on data :id)]
      (subject/index-select 1 :using index :keys ["rest"]) => (just {"rest" ..rest11..}
                                                                 {"rest" ..rest12..}
                                                                 :in-any-order)
      (subject/index-select 1 :using index :keys ["rest"] :prefix "XX")
      => (just {"XXrest" ..rest11..}
               {"XXrest" ..rest12..}
               :in-any-order)))

  (fact "works with string keys and prefixes"
    (let [data [{"id" 1 "rest" ..rest1..} {"id" 2 "rest" ..rest2..}]
          index (subject/one-to-one-index-on data "id")]
      (fact "both can be strings"
        (subject/index-select 1 :using index :prefix "pre-") => {"pre-id" 1 "pre-rest" ..rest1..})
      (fact "note that it is the type of the original key that determines type of result key"
        (subject/index-select 1 :using index :prefix :pre-) => {"pre-id" 1 "pre-rest" ..rest1..})))


(fact "you can extend maps via indexes"
  (fact "one-to-one tables"
    (let [original-map {:id 1 :foreign_id "a" :rest ..rest1..}
          foreign-table [{:id "a" :val "fa"} {:id "b" :val "fb"}]
          foreign-index (subject/one-to-one-index-on foreign-table :id)]

      (subject/extend-map original-map :using foreign-index :via :foreign_id
                          :keys [:val] :prefix "foreign-")
      => {:id 1 :foreign_id "a" :rest ..rest1.. :foreign-val "fa"}))

  (fact "one-to-one tables with compound keys"
    (let [original-map {:id 1 :foreign_id_alpha "a" :foreign_id_num 1 :rest ..rest1..}
          foreign-table [{:alpha "a" :id 1 :val "fa"} {:alpha "b" :id "2" :val "fb"}]
          foreign-index (subject/one-to-one-index-on foreign-table [:alpha :id])]

      ;; TODO: This test fails under 1.6, but not 1.7 or 1.8.
      (subject/extend-map original-map :using foreign-index :via [:foreign_id_alpha :foreign_id_num]
                          :keys [:val] :prefix "foreign-")
      => {:id 1 :foreign_id_alpha "a" :foreign_id_num 1 :rest ..rest1..
          :foreign-val "fa"}))


  (fact "one-to-many tables merge under a given key"
    (let [foreign-table [{:id "a" :val "fa"} {:id "a" :val "fb"}]
          foreign-index (subject/one-to-many-index-on foreign-table :id)]

      (fact "you can add the key"
        (let [original-map {:id 1 :foreign_id "a" :rest ..rest1..}
              result (subject/extend-map original-map :using foreign-index :via :foreign_id
                                         :into :foreign-data
                                         :keys [:val] :prefix "f-")]

          result => (contains original-map)
          (:foreign-data result) => (just [{:f-val "fa"} {:f-val "fb"}]
                                          :in-any-order)))

      (fact "you can append to existing values"
        (let [original-map {:id 1 :foreign_id "a" :rest ..rest1..
                            :foreign-data ["already here"]}
              result (subject/extend-map original-map :using foreign-index :via :foreign_id
                                         :into :foreign-data
                                         :keys [:val] :prefix "f-")]
          (:foreign-data result) => (just ["already here" {:f-val "fa"} {:f-val "fb"}]
                                          :in-any-order)))))

  )


(future-fact "error handling")

) ; when>=1-7
