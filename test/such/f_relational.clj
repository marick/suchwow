(ns such.f-relational
  (:require [such.versions :refer [when>=1-7 when>=1-6]]
            [such.relational :as subject]
            [such.metadata :as meta]
            [clojure.set :as set]
            [clojure.pprint :refer [pprint]]
            [midje.sweet :refer :all]))

; (when>=1-6


(fact "indices"
  (fact "one-to-one indices"
    (let [data [{:id 1 :rest ..rest1..} {:id 2 :rest ..rest2..}]
          index (subject/one-to-one-index-on data :id)]
      (subject/select-map 1 :using index) => {:id 1 :rest ..rest1..}))

  (fact "compound-to-one indices"
    (let [data [{:id 1 :pk 1 :rest ..rest11..}
                {:id 1 :pk 2 :rest ..rest12..}
                {:id 2 :pk 2 :rest ..rest22..}]
          index (subject/compound-to-one-index-on data [:id :pk])]
      (subject/select-map [1 1] :using index) => (first data)
      (subject/select-map [1 2] :using index) => (second data)))

  (fact "options"
    (let [data [{:id 1 :rest ..rest1..} {:id 2 :rest ..rest2..}]
          index (subject/one-to-one-index-on data :id)]
      (fact "can limit the number of keys returned"
        (subject/select-map 1 :using index :only [:rest]) => {:rest ..rest1..})
      (fact "can add a prefix to keys as keyword..."
        (subject/select-map 1 :using index :prefix :pre-) => {:pre-id 1 :pre-rest ..rest1..})
      (fact "both"
        (subject/select-map 1 :using index :only [:rest] :prefix :pre-) => {:pre-rest ..rest1..})

      (fact "options can also be provided as maps"
        (subject/select-map 1 {:using index :only [:rest]}) => {:rest ..rest1..})))

  (fact "works with string keys and prefixes"
    (let [data [{"id" 1 "rest" ..rest1..} {"id" 2 "rest" ..rest2..}]
          index (subject/one-to-one-index-on data "id")]
      (fact "both can be strings"
        (subject/select-map 1 :using index :prefix "pre-") => {"pre-id" 1 "pre-rest" ..rest1..})
      (fact "note that it is the type of the original key that determines type of result key"
        (subject/select-map 1 :using index :prefix :pre-) => {"pre-id" 1 "pre-rest" ..rest1..}))))


(fact "you can extend maps via indexes"
  (fact "one-to-one tables"
    (let [original-map {:id 1 :foreign_id "a" :rest ..rest1..}
          foreign-table [{:id "a" :val "fa"} {:id "b" :val "fb"}]
          foreign-index (subject/one-to-one-index-on foreign-table :id)]

      (subject/extend-map original-map :using foreign-index :via :foreign_id
                          :only [:val] :prefix "foreign-")
      => {:id 1 :foreign_id "a" :rest ..rest1.. :foreign-val "fa"}))

  (fact "compound-to-one tables"
    (let [original-map {:id 1 :foreign_id_alpha "a" :foreign_id_num 1 :rest ..rest1..}
          foreign-table [{:alpha "a" :id 1 :val "fa"} {:alpha "b" :id "2" :val "fb"}]
          foreign-index (subject/compound-to-one-index-on foreign-table [:alpha :id])]

      (subject/extend-map original-map :using foreign-index :via [:foreign_id_alpha :foreign_id_num]
                          :only [:val] :prefix "foreign-")
      => {:id 1 :foreign_id_alpha "a" :foreign_id_num 1 :rest ..rest1..
          :foreign-val "fa"}))


  )



;; (facts "about extracting a simple one-to-one index from maps"
;;   (subject/one-to-one-index-on :pk [{:pk 1, :rest 2} {:pk 2, :rest 3}])
;;   => {1 {:pk 1, :rest 2}
;;       2 {:pk 2, :rest 3}}

;;   (fact "works with non-keyword keys"
;;     (subject/one-to-one-index-on "i" [{"i" 1, :rest 2} {"i" 2, :rest 3}])
;;     => {1 {"i" 1, :rest 2}
;;         2 {"i" 2, :rest 3}})

;;   (fact "you can also stash a prefix string for future use"
;;     (let [result (subject/one-to-one-index-on :pk [{:pk 1, :rest 2} {:pk 2, :rest 3}] :pre-)]
;;       (subject/index-prefix result) => "pre-")) ; note stringification

;;   (fact "a prefix can also be a string"
;;     (let [result (subject/one-to-one-index-on :pk [{:pk 1, :rest 2} {:pk 2, :rest 3}] "pre-")]
;;       (subject/index-prefix result) => "pre-")))

;; (facts "about extracting a one-to-many index from maps"
;;   ;; Simple test, since it shares code with above.
;;   (let [r (subject/one-to-many-index-on :foreign_id
;;                                         [{:pk 1, :foreign_id 2}
;;                                          {:pk 2, :foreign_id 2}
;;                                          {:pk 3, :foreign_id 8}])]
;;     (get r 2) => (just [{:pk 1, :foreign_id 2} {:pk 2, :foreign_id 2}] :in-any-order)
;;     (get r 8) => (just [{:pk 3, :foreign_id 8}] :in-any-order)))


;; (facts "about merging via a foreign key"
;;   (let [subtable [{:subpk ..sub1.. :other ..other1..} {:subpk ..sub2.. :other ..other2..}]]
;;     (fact "a starting - not so useful - version"
;;       (let [sub-index (subject/one-to-one-index-on :subpk subtable)]
;;         (subject/merge-related-table {:pk ..pk.. :foreign ..sub1..} :foreign sub-index)
;;         => {:pk ..pk.. :foreign ..sub1.. :subpk ..sub1.. :other ..other1..}))

;;     (fact "it is better if the map contains a prefix"
;;       (let [sub-index (subject/one-to-one-index-on :subpk subtable :sub_)]
;;         (subject/merge-related-table {:pk ..pk.. :foreign ..sub1..} :foreign sub-index)
;;         => {:pk ..pk.. :foreign ..sub1.. :sub_subpk ..sub1.. :sub_other ..other1..}))

;;     (fact "you can also select a subset of the foreign map's keys"
;;       (let [sub-index (subject/one-to-one-index-on :subpk subtable)]
;;         (subject/merge-related-table {:pk ..pk.. :foreign ..sub1..} :foreign sub-index [:other])
;;         => {:pk ..pk.. :foreign ..sub1.. :other ..other1..}))))


;; (facts "about flattening lookup"
;;   (let [problems [{:id 1, :name "problem1"}
;;                   {:id 2, :name "problem2"}]
;;         index:problem-by-id (subject/one-to-one-index-on :id problems "problem_")
;;         icd10-codes [{:code "one", :description "code1"}
;;                      {:code "two", :description "code2"}
;;                      {:code "three", :description "code3"}]
;;         index:icd10-by-code (subject/one-to-one-index-on :code icd10-codes "icd10_")
;;         icd10-code-problem-assignments [{:id 1, :problem_id 1, :code "one"}
;;                                         {:id 2, :problem_id 2, :code "two"}
;;                                         {:id 3, :problem_id 2, :code "three"}]
;;         index:icd10-code->problem-id (subject/one-to-one-index-on :code icd10-code-problem-assignments)]

;;     (subject/flattening-lookup 1 index:problem-by-id [:name]) => {:name "problem1"}

;;     (subject/flattening-lookup "one" index:icd10-code->problem-id [:problem_id :code]
;;                                [:problem_id index:problem-by-id [:name]])
;;     => {:problem_id 1, :code "one", :problem_name "problem1"}

;;     (subject/flattening-lookup "one" index:icd10-code->problem-id [:problem_id :code]
;;                                [:problem_id index:problem-by-id [:name]]
;;                                [:code index:icd10-by-code [:description]])
;;     => {:problem_id 1, :code "one", :problem_name "problem1", :icd10_description "code1"}))

;) ; when>=1-6

;(when>=1-7 ; this uses structural-typing, which requires 1-7

;; (require '[structural-typing.type :as type])


;; ;;; Here is the construction of a (medical) Problem from an ICD-10 code.
;; ;;; https://en.wikipedia.org/wiki/ICD-10

;; ;;; Imagine this is found in a namespace that has authority over the construction of
;; ;;; Problem maps. Its various `from-` functions may traverse foreign keys into different
;; ;;; tables to assemble the maps. The end result must have these keys:

;; (def problem-keys [:problem_id :problem_name :icd10_code :icd10_description])

;; ;;; ... which is checked like this:

;; (def type-repo (-> type/empty-type-repo
;;                    (type/replace-error-handler type/throwing-error-handler)
;;                    (type/named :Problem (apply type/requires problem-keys))))
;; (def this-is-a-problem #(type/built-like type-repo :Problem %))


;; ;;; We work from relational tables (vectors of maps) that have been
;; ;;; fetched from a relational database or constructed by hand.

;; (def problems [{:id 1, :name "problem1"}
;;                {:id 2, :name "problem2"}])

;; (def icd10-codes [{:code "one", :description "code1"}
;;                   {:code "two", :description "code2"}
;;                   {:code "three", :description "code3"}])

;; (def icd10-code-problem-assignments [{:id 1, :code "one",  :problem_id 1}
;;                                      {:id 2, :code "two",  :problem_id 2}
;;                                      ;; Note next one is broken
;;                                      {:id 3, :code "three" :problem_id nil}])


;; ;;; For each table, we can construct "indexes" that provide fast access to
;; ;;; individual maps within the "tables". When elements are fetched via the
;; ;;; index, they can have a prefix prepended to them. (Think of `SELECT table.column AS col`,
;; ;;; except enforced for all queries.)

;; (def index:problem-by-id (subject/one-to-one-index-on :id problems "problem_"))
;; (def index:icd10-by-code (subject/one-to-one-index-on :code icd10-codes "icd10_"))
;; (def index:icd10-code->problem-id  ; this contributes nothing to the final problem, so no prefix.
;;   (subject/one-to-one-index-on :code icd10-code-problem-assignments))

;; ;;; `from` functions build up a result in the following stages:
;; ;;;   1. Select the initial map from the value of a key in an index.
;; ;;;   2.   Repeat: follow a foreign key and merge selected key/values.
;; ;;;   3. Rename any keys that are not to taste.
;; ;;;   4. Restrict the resulting map to the keys allowed in a Problem.
;; ;;;   5. Blow up unless the Problem is well-formed.

;; ;;; Note: in each step of the process, there's no equivalent to `SELECT *`. My experience
;; ;;; has been that it's a lot easier to understand what's going on if you can see where
;; ;;; each key is coming from, especially if the original column names are imperfect.

;; (defn from-icd10
;;   [icd10-code]
;;   (-> icd10-code
;;       (subject/flattening-lookup
;;          index:icd10-code->problem-id [:problem_id :code]
;;          [:problem_id index:problem-by-id [:name]]
;;          [:code index:icd10-by-code [:description]])
;;       (set/rename-keys {:code :icd10_code})
;;       (select-keys problem-keys)
;;       this-is-a-problem))

;; (fact
;;   (from-icd10 "one") => {:problem_id 1 :problem_name "problem1",
;;                          :icd10_code "one", :icd10_description "code1"}
;;   (from-icd10 "three") => (throws #":problem_id must exist and be non-nil"))


;) ; >= 1.7
