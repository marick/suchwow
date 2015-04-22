(ns such.clojure.f-core
  (:use such.clojure.core)
  (:use midje.sweet))

(fact "shorthand and types are available"
  ((ns-refers *ns*) 'third) => #'such.clojure.core/third

  (third [1 2 3]) => 3
  (namespace? *ns*) => true)

(fact "selected vars can be immigrated"
  ((ns-refers *ns*) 'selection) => #'such.clojure.core/selection
  (fn? selection) => true
  (union #{1 2} #{2 3}) => #{1 2 3}

  (fact "immigrated private vars are not visible here"
    ((ns-refers *ns*) 'move-var!) => nil))

(fact "prefixed vars are only available via the prefix"
  ((ns-refers *ns*) 'blank?) => nil
  ((ns-refers *ns*) 'str-blank?) => var?
  (str-blank? " ") => true)
