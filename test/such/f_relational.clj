(ns such.f-relational
  (:require [such.versions :refer [when>=1-7]]
            [such.relational :as subject]
            [such.metadata :as meta]
            [clojure.set :as set]
            [clojure.pprint :refer [pprint]]
            [midje.sweet :refer :all]))

(fact "confirm that clojure.set imports are really here"
  (fact index
    (subject/index [{:a 1}] [:a]) => { {:a 1} #{ {:a 1} }}

    (subject/index [ {:a 1} {:b 1} {:a 1, :b 1} {:c 1}] [:a :b])
    =>  {  {:a 1, :b 1}    #{ {:a 1 :b 1} }
           {:a 1      }    #{ {:a 1} }
           {      :b 1}    #{ {:b 1} }
           {          }    #{ {:c 1} }})

  (fact join
    (let [has-a-and-b [{:a 1, :b 2} {:a 2, :b 1} {:a 2, :b 2}]
          has-b-and-c [{:blike 1, :c 2} {:blike 2, :c 1} {:blike 2, :c 2}]]
      (subject/join has-a-and-b has-b-and-c {:b :blike})
      => #{{:a 1, :b 2, :blike 2, :c 1} {:a 1, :b 2, :blike 2, :c 2}
           {:a 2, :b 1, :blike 1, :c 2} {:a 2, :b 2, :blike 2, :c 1}
           {:a 2, :b 2, :blike 2, :c 2}}))
  )


(when>=1-7


;;;;; The two one-level indexes

(fact "one-to-one indices"
  (let [data [{:id 1 :rest ..rest1..} {:id 2 :rest ..rest2..}]
        index (subject/one-to-one-index-on data :id)]
    (subject/index-select 1 :using index) => {:id 1 :rest ..rest1..})

  (fact "one-to-one indices where the keys are compound"
    (let [data [{:id 1 :pk 1 :rest ..rest11..}
                {:id 1 :pk 2 :rest ..rest12..}
                {:id 2 :pk 2 :rest ..rest22..}]
          index (subject/one-to-one-index-on data [:id :pk])]
      (subject/index-select [1 1] :using index) => (first data)
      (subject/index-select [1 2] :using index) => (second data)))

  (fact "options used when selecting"
    (let [data [{:id 1 :rest ..rest1..} {:id 2 :rest ..rest2..}]
          index (subject/one-to-one-index-on data :id)]
      (fact "can limit the number of keys returned"
        (subject/index-select 1 :using index :keys [:rest]) => {:rest ..rest1..})
      (fact "can add a prefix to keys as keyword..."
        (subject/index-select 1 :using index :prefix :pre-) => {:pre-id 1 :pre-rest ..rest1..})
      (fact "both"
        (subject/index-select 1 :using index :keys [:rest] :prefix :pre-) => {:pre-rest ..rest1..})

      (fact "options can also be provided as maps"
        (subject/index-select 1 {:using index :keys [:rest]}) => {:rest ..rest1..}))))


(fact "one-to-many indices"
  (let [data [{:id 1 :rest ..rest11..}
              {:id 1 :rest ..rest12..}
              {:id 2 :rest ..rest22..}]
        index (subject/one-to-many-index-on data :id)]
    (subject/index-select 1 :using index) => (just (first data) (second data) :in-any-order))

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
               :in-any-order))))

;;; A third type of index: The Combined Index

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

  (fact "selecting along a path (a building block)"
    (fact "1-N 1-N 1-N"
      (#'subject/select-along-path "top" one-to-many-top-index
                                   :foreign one-to-many-middle-index
                                   :foreign one-to-many-bottom-index)
      => (just {:id "bottom" :tag 1} {:id "bottom" :tag 2}
               {:id "bottom2" :tag 3} {:id "bottom2" :tag 4} :in-any-order))


    (fact "1-N 1-1 1-N"
      (#'subject/select-along-path "top" one-to-many-top-index
                                   :foreign one-to-one-middle-index
                                   :foreign one-to-many-bottom-index)
      => (just {:id "bottom" :tag 1} {:id "bottom" :tag 2}
               {:id "bottom2" :tag 3} {:id "bottom2" :tag 4} :in-any-order))


    (fact "1-N 1-1 1-1"
      (#'subject/select-along-path "top" one-to-many-top-index
                                   :foreign one-to-one-middle-index
                                   :foreign one-to-one-bottom-index)
      => (just {:id "bottom"} {:id "bottom2"} :in-any-order))

    (fact "1-1 1-N 1-1"
      (#'subject/select-along-path "top" one-to-one-top-index
                                   :foreign one-to-many-middle-index
                                   :foreign one-to-one-bottom-index)
      => (just {:id "bottom"} {:id "bottom2"} :in-any-order))

    (fact "1-1 1-1 1-N"
      (#'subject/select-along-path "top" one-to-one-top-index
                                   :foreign one-to-one-middle-index
                                   :foreign one-to-many-bottom-index)
      => (just {:id "bottom" :tag 1} {:id "bottom" :tag 2} :in-any-order))


    (fact "1-1 1-1 1-1"
      ;; Note that it does *not* remove the singleton wrapper around the return value.
      (#'subject/select-along-path "top" one-to-one-top-index
                                   :foreign one-to-one-middle-index
                                   :foreign one-to-one-bottom-index)
      => (just {:id "bottom"})))


    (fact "making an index and then selecting"
      (fact "1-N 1-N 1-N"
        (let [combined-index (subject/combined-index-on one-to-many-top-index
                                                        :foreign one-to-many-middle-index
                                                        :foreign one-to-many-bottom-index)]
          (subject/index-select "top" :using combined-index)
          => (just {:id "bottom" :tag 1} {:id "bottom" :tag 2}
                   {:id "bottom2" :tag 3} {:id "bottom2" :tag 4} :in-any-order)))

      (fact "1-N 1-1 1-N"
        (let [combined-index (subject/combined-index-on one-to-many-top-index
                                                        :foreign one-to-one-middle-index
                                                        :foreign one-to-many-bottom-index)]
          (subject/index-select "top" :using combined-index)
          => (just {:id "bottom" :tag 1} {:id "bottom" :tag 2}
                   {:id "bottom2" :tag 3} {:id "bottom2" :tag 4} :in-any-order)))

      (fact "1-N 1-1 1-1"
        (let [combined-index (subject/combined-index-on one-to-many-top-index
                                                        :foreign one-to-one-middle-index
                                                        :foreign one-to-one-bottom-index)]
          (subject/index-select "top" :using combined-index)
          => (just {:id "bottom"} {:id "bottom2"} :in-any-order)))

      (fact "1-1 1-N 1-1"
        (let [combined-index (subject/combined-index-on one-to-one-top-index
                                                        :foreign one-to-many-middle-index
                                                        :foreign one-to-one-bottom-index)]
          (subject/index-select "top" :using combined-index)
          => (just {:id "bottom"} {:id "bottom2"} :in-any-order)))

      (fact "1-1 1-1 1-N"
        (let [combined-index (subject/combined-index-on one-to-one-top-index
                                                        :foreign one-to-one-middle-index
                                                        :foreign one-to-many-bottom-index)]
          (subject/index-select "top" :using combined-index)
          => (just {:id "bottom" :tag 1} {:id "bottom" :tag 2} :in-any-order)))


      (fact "1-1 1-1 1-1"
        (let [combined-index (subject/combined-index-on one-to-one-top-index
                                                        :foreign one-to-one-middle-index
                                                        :foreign one-to-one-bottom-index)]
          (subject/index-select "top" :using combined-index)
          => {:id "bottom"})))


    (fact "a less abstract example"
      (let [people [{:id 1 :note "ruler of one country" :name "onesie"}
                    {:id 2 :note "ruler of two countries" :name "twosie"}
                    {:id 0 :note "ruler of no countries" :name "nonesie"}]

            rulerships [{:id 1 :country_code "ESP" :person_id 1}
                        {:id 2 :country_code "NOR" :person_id 2}
                        {:id 3 :country_code "ESP" :person_id 2}]

            countries [{:id 1 :country_code "ESP" :gdp 1690}
                       {:id 2 :country_code "NOR" :gdp  513}]


            index:person-by-id (subject/one-to-one-index-on people :id)
            index:rulership-by-person-id (subject/one-to-many-index-on rulerships :person_id)
            index:country-by-country-code (subject/one-to-one-index-on countries :country_code)


            index:countries-by-person-id (subject/combined-index-on index:rulership-by-person-id
                                                                    :country_code
                                                                    index:country-by-country-code)]



        (subject/index-select 1 :using index:countries-by-person-id :keys [:gdp])
        => [{:gdp 1690}]
        (subject/index-select 2 :using index:countries-by-person-id :keys [:gdp])
        => (just {:gdp 1690} {:gdp 513} :in-any-order)
        (subject/index-select 0 :using index:countries-by-person-id :keys [:gdp])
        => empty?

        ;; Use with `extend-map` (fully tested elsewhere)
        (-> (subject/index-select 2 :using index:person-by-id :keys [:name :id])
            (subject/extend-map :using index:countries-by-person-id
                                :via :id
                                :keys [:country_code :gdp]
                                :into :countries))
        => (just {:name "twosie" :id 2,
                  :countries (just {:country_code "NOR" :gdp 513}
                                   {:country_code "ESP" :gdp 1690}
                                   :in-any-order)}))))


;;; In addition to selecting elements, you can extend maps (similar to joins)

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
    (future-fact "why does this test fail 1.6 but not 1.7 or 1.8?")
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

;;;; Miscellany


(fact "Everything works with string keys and prefixes"
  (future-fact "more is probably needed")
  (let [data [{"id" 1 "rest" ..rest1..} {"id" 2 "rest" ..rest2..}]
        index (subject/one-to-one-index-on data "id")]
    (fact "both can be strings"
      (subject/index-select 1 :using index :prefix "pre-") => {"pre-id" 1 "pre-rest" ..rest1..})
    (fact "note that it is the type of the original key that determines type of result key"
      (subject/index-select 1 :using index :prefix :pre-) => {"pre-id" 1 "pre-rest" ..rest1..})))



(future-fact "error handling")

) ; when>=1-7
