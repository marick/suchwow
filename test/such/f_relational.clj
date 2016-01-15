(ns such.f-relational
  (:require [such.relational :as subject]
            [such.metadata :as meta]
            [clojure.set :as set]
            [clojure.pprint :refer [pprint]]
            [structural-typing.type :as type]
            [midje.sweet :refer :all]))

(facts "about extracting a simple index from maps"
  (subject/simple-index-on :pk [{:pk 1, :rest 2} {:pk 2, :rest 3}])
  => {1 {:pk 1, :rest 2}
      2 {:pk 2, :rest 3}}

  (fact "works with non-keyword keys"
    (subject/simple-index-on "i" [{"i" 1, :rest 2} {"i" 2, :rest 3}])
    => {1 {"i" 1, :rest 2}
        2 {"i" 2, :rest 3}})

  (fact "you can also stash a prefix string for future use"
    (let [result (subject/simple-index-on :pk [{:pk 1, :rest 2} {:pk 2, :rest 3}] :pre-)]
      (subject/index-prefix result) => "pre-")) ; note stringification

  (fact "a prefix can also be a string"
    (let [result (subject/simple-index-on :pk [{:pk 1, :rest 2} {:pk 2, :rest 3}] "pre-")]
      (subject/index-prefix result) => "pre-")))

(facts "about merging via a foreign key"
  (let [subtable [{:subpk ..sub1.. :other ..other1..} {:subpk ..sub2.. :other ..other2..}]]
    (fact "a starting - not so useful - version"
      (let [sub-index (subject/simple-index-on :subpk subtable)]
        (subject/has-one:merge {:pk ..pk.. :foreign ..sub1..} :foreign sub-index)
        => {:pk ..pk.. :foreign ..sub1.. :subpk ..sub1.. :other ..other1..}))

    (fact "it is better if the map contains a prefix"
      (let [sub-index (subject/simple-index-on :subpk subtable :sub_)]
        (subject/has-one:merge {:pk ..pk.. :foreign ..sub1..} :foreign sub-index)
        => {:pk ..pk.. :foreign ..sub1.. :sub_subpk ..sub1.. :sub_other ..other1..}))

    (fact "you can also select a subset of the foreign map's keys"
      (let [sub-index (subject/simple-index-on :subpk subtable)]
        (subject/has-one:merge {:pk ..pk.. :foreign ..sub1..} :foreign sub-index [:other])
        => {:pk ..pk.. :foreign ..sub1.. :other ..other1..}))))


(facts "about flattening lookup"
  (let [problems [{:id 1, :name "problem1"}
                  {:id 2, :name "problem2"}]
        index:problem-by-id (subject/simple-index-on :id problems "problem_")
        icd10-codes [{:code "one", :description "code1"}
                     {:code "two", :description "code2"}
                     {:code "three", :description "code3"}]
        index:icd10-by-code (subject/simple-index-on :code icd10-codes "icd10_")
        icd10-code-problem-assignments [{:id 1, :problem_id 1, :code "one"}
                                        {:id 2, :problem_id 2, :code "two"}
                                        {:id 3, :problem_id 2, :code "three"}]
        index:icd10-code->problem-id (subject/simple-index-on :code icd10-code-problem-assignments)]

    (subject/flattening-lookup 1 index:problem-by-id [:name]) => {:name "problem1"}

    (subject/flattening-lookup "one" index:icd10-code->problem-id [:problem_id :code]
                               [:problem_id index:problem-by-id [:name]])
    => {:problem_id 1, :code "one", :problem_name "problem1"}

    (subject/flattening-lookup "one" index:icd10-code->problem-id [:problem_id :code]
                               [:problem_id index:problem-by-id [:name]]
                               [:code index:icd10-by-code [:description]])
    => {:problem_id 1, :code "one", :problem_name "problem1", :icd10_description "code1"}))


;;; Here is the construction of a (medical) Problem from an ICD-10 code.
;;; https://en.wikipedia.org/wiki/ICD-10

;;; Imagine this is found in a namespace that has authority over the construction of
;;; Problem maps. Its various `from-` functions may traverse foreign keys into different
;;; tables to assemble the maps. The end result must have these keys:

(def problem-keys [:problem_id :problem_name :icd10_code :icd10_description])

;;; ... which is checked like this:

(def type-repo (-> type/empty-type-repo
                   (type/replace-error-handler type/throwing-error-handler)
                   (type/named :Problem (apply type/requires problem-keys))))
(def confirm-type #(type/built-like type-repo :Problem %))


;;; We work from relational tables (vectors of maps) that have been
;;; fetched from a relational database or constructed by hand.

(def problems [{:id 1, :name "problem1"}
               {:id 2, :name "problem2"}])

(def icd10-codes [{:code "one", :description "code1"}
                  {:code "two", :description "code2"}
                  {:code "three", :description "code3"}])

(def icd10-code-problem-assignments [{:id 1, :problem_id 1, :code "one"}
                                     {:id 2, :problem_id 2, :code "two"}
                                     {:id 3, :problem_id 2, :code "three"}])


;;; For each table, we can construct "indexes" that provide fast access to
;;; individual maps within the "tables". When elements are fetched via the
;;; index, they can have a prefix prepended to them. (Think of `SELECT table.column AS col`,
;;; except enforced for all queries.)

(def index:problem-by-id (subject/simple-index-on :id problems "problem_"))
(def index:icd10-by-code (subject/simple-index-on :code icd10-codes "icd10_"))
(def index:icd10-code->problem-id  ; this contributes nothing to the final problem, so no prefix.
  (subject/simple-index-on :code icd10-code-problem-assignments))

;;; `from` functions build up a result in the following stages:
;;;   1. Select the initial map from the value of a key in an index.
;;;   2.   Repeat expanding merging in the results of following foreign keys.
;;;   3. Rename any keys that are not to taste.
;;;   4. Restrict the resulting map to the keys allowed in a Problem.
;;;   5. Blow up unless the Problem is well-formed.

;;; Note: in each step of the process, there's no equivalent to `SELECT *`. My experience
;;; has been that it's a lot easier to understand what's going on if you can see where
;;; each key is coming from, especially if the original column names are imperfect.

(defn from-icd10
  [icd10-code]
  (-> icd10-code
      (subject/flattening-lookup
         index:icd10-code->problem-id [:problem_id :code]
         [:problem_id index:problem-by-id [:name]]
         [:code index:icd10-by-code [:description]])
      (set/rename-keys {:code :icd10_code})
      (select-keys problem-keys)
      confirm-type))

(fact
  (from-icd10 "one") => {:problem_id 1 :problem_name "problem1",
                         :icd10_code "one", :icd10_description "code1"})
