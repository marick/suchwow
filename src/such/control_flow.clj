(ns such.control-flow)

(defmacro branch-on
  "    (branch-on (str \"one\" \"two\")
         vector?   :vector
         string?   :string
         :else     :unknown)

  Evaluates the `value-form` once, then checks that value against
  each predicate in the cond-like body. The value after the first
  matching predicate is returned. If there is no match and an `:else`
  clause is present, its value is returned, otherwise `nil`.
"
  [value-form & body]
  (let [value-sym (gensym "value-form-")
        cond-pairs (mapcat (fn [[branch-pred-form branch-val-form]]
                             (let [test (if (= branch-pred-form :else)
                                          :else
                                          `(~branch-pred-form ~value-sym))]
                             `(~test ~branch-val-form)))
                           (partition 2 body))]
    
    `(let [~value-sym ~value-form]
       (cond ~@cond-pairs))))

(defmacro let-maybe
  "Like `let` except that if any symbol would be bound to a `nil`, the
   entire expression immediately short-circuits and returns `nil`.

       (let-maybe [v []
                   f (first v)
                   _ (throw (new Exception))]
          (throw (new Exception)))
       => nil
"
  [bindings & body]
  (if (empty? bindings)
    `(do ~@body)
    `(when-some [~@(take 2 bindings)]
       (let-maybe [~@(drop 2 bindings)] ~@body))))
