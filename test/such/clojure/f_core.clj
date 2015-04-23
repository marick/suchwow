(ns such.clojure.f-core
  (:use such.clojure.core)
  (:use midje.sweet))

(fact "shorthand and types are available"
  ((ns-refers *ns*) 'third) => #'such.clojure.core/third

  (third [1 2 3]) => 3
  (namespace? *ns*) => true

  (fact "note that a var with no value is not immigrated"
    (find-var 'such.shorthand/this-var-has-no-value-and-is-used-in-testing) => truthy
    (find-var 'such.clojure.core/this-var-has-no-value-and-is-used-in-testing) => nil
    (find-var 'such.clojure.f-core/this-var-has-no-value-and-is-used-in-testing) => nil))

(fact "metadata is stored appropriately"
  (meta #'third) => (contains {:doc #"Return the third element"
                               :ns (find-ns 'such.clojure.core)}))

(fact "selected vars can be immigrated"
  ;; Note that union was immigrated twice in a row.
  ((ns-refers *ns*) 'selection) => #'such.clojure.core/selection
  (fn? selection) => true
  (union #{1 2} #{2 3}) => #{1 2 3}
  (intersection #{1 2} #{2 3}) => #{2}

  (fact "immigrated private vars are not visible here"
    ((ns-refers *ns*) 'move-var!) => nil))

(fact "prefixed vars are only available via the prefix"
  ((ns-refers *ns*) 'blank?) => nil
  ((ns-refers *ns*) 'str-blank?) => var?
  (str-blank? " ") => true)




(fact "a referred value (has-root-value?was silently overwritten"
  (has-root-value? #'even?) => true
  (fact "... as was a value "
    (root-value #'even?) => (exactly even?)))

