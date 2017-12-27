(ns such.readable
  "Stringify nested structures such that all functions - and particular values of your
   choice - are displayed in a more readable way. [[value-string]] and [[fn-symbol]] are
   the key functions."
  (:refer-clojure :exclude [print])
  (:require [such.symbols :as symbol]
            [such.types :as type]
            [clojure.string :as str]
            [clojure.repl :as repl]
            [com.rpl.specter :as specter]))

;;; What is stringified is controlled by two dynamically-bound variables.

(def default-function-elaborations
  "Anonymous functions are named `fn` and functions are surrounded by `<>`"
  {:anonymous-name "fn" :surroundings "<>"})

(def ^:private ^:dynamic *function-elaborations*
  {:anonymous-name "fn" :surroundings "<>"})

(defn set-function-elaborations!
  "Control the way functions are prettified. Note: this does not override
   any value changed with `with-function-elaborations`.

         (set-function-elaborations! {:anonymous-name 'anon :surroundings \"\"})
"
  [{:keys [anonymous-name surroundings] :as all}]
  (alter-var-root #'*function-elaborations* (constantly all)))

(defmacro with-function-elaborations
  "Change the function elaborations, execute the body, and revert the
   elaborations.

        (with-function-elaborations {:anonymous-name 'fun :surroundings \"{{}}\"}
          (fn-symbol (fn []))) => {{fun}}
"
  [{:keys [anonymous-name surroundings] :as all} & body]
  `(binding [*function-elaborations* ~all]
     ~@body))

(def ^:private ^:dynamic *translations*
  "This atom contains the map from values->names that [[with-translations]] and
   [[value-strings]] use."
  (atom {}))

(defn- translatable? [x]
  (contains? (deref *translations*) x))

(defn- translate [x]
  (get (deref *translations*) x))

(defn forget-translations!
  "There is a global store of translations from values to names, used by
   [[with-translations]] and [[value-strings]]. Empty it."
  []
  (reset! *translations* {}))

(defn instead-of
  "Arrange for [[value-string]] to show `value` as `show`. `show` is typically
   a symbol, but can be anything."
  [value show]
  (swap! *translations* assoc value show))

(defmacro with-translations
  "Describe a set of value->name translations, then execute the body
   (which presumably contains a call to [[value-string]]).

         (with-translations [5 'five
                             {1 2} 'amap]
           (value-string {5 {1 2}
                          :key [:value1 :value2 5]}))
         => \"{five amap, :key [:value1 :value2 five]}\"
"
  [let-style & body]
  `(binding [*translations* (atom {})]
    (doseq [pair# (partition 2 ~let-style)]
      (apply instead-of pair#))
    ~@body))

(defn rename
  "Produce a new function from `f`. It has the same behavior and metadata,
   except that [[fn-symbol]] and friends will use the given `name`.

   Note: `f` may actually be any object that allows metadata. That's irrelevant
   to `fn-symbol`, which accepts only functions, but it can be used to affect
   the output of [[value-string]]."

  [f name]
  (with-meta f (merge (meta f) {::name name})))

(defn- generate-name [f base-name anonymous-names]
  (if (contains? @anonymous-names f)
    (@anonymous-names f)
    (let [name (if (empty? @anonymous-names)
                 base-name
                 (str base-name "-" (+ 1 (count @anonymous-names))))]
      (swap! anonymous-names assoc f name)
      name)))

(defn- super-demunge [f]
  (-> (str f)
      repl/demunge
      (str/split #"/")
      last
      (str/split #"@")
      first
      (str/split #"--[0-9]+$")
      first
      ;; last clause required by 1.5.X
      (str/replace "-COLON-" ":")))

(def ^:private show-as-anonymous? #{"fn" "clojure.lang.MultiFn"})

(defn elaborate-fn-symbol
  "A more customizable version of [[fn-symbol]]. Takes `f`, which *must* be a function
   or multimethod. In all cases, the return value is a symbol where `f`'s name is embedded
   in the `surroundings`, a string. For example, if the surroundings are \"<!!>\", a
   result would look like `<!cons!>`.

   `f`'s name is found by these rules, checked in
   order:

   * `f` has had a name assigned with `rename`.

   * `f` is a key in `(deref anonymous-names)`. The value is its name.

   * The function had a name assigned by `defn`, `let`,
     or the seldom used \"named lambda\": `(fn name [...] ...)`.
     Note that multimethods do not have accessible names in current versions
     of Clojure. They are treated as anonymous functions.

   * The function is anonymous and there are no other anonymous names. The name is
     `anonymous-name`, which is also stored in the `anonymous-names` atom.

   * After the first anonymous name, the names are `<anonymous-name>-2` `<anonymous-name>-3`
     and so on.

   In the single-argument version, the global or default elaborations are used,
   and `anonymous-names` is empty. See [[set-function-elaborations!]].
"
  ([f {:keys [anonymous-name surroundings]} anonymous-names]
     (let [candidate (if (contains? (meta f) ::name)
                       (get (meta f) ::name)
                       (super-demunge f))]
       (symbol/from-concatenation [(subs surroundings 0 (/ (count surroundings) 2))
                                   (if (show-as-anonymous? candidate)
                                     (generate-name f anonymous-name anonymous-names)
                                     candidate)
                                   (subs surroundings (/ (count surroundings) 2))])))
  ([f]
     (elaborate-fn-symbol f *function-elaborations* (atom {}))))


(defn fn-symbol
  "Transform `f` into a symbol with a more pleasing string representation.
   `f` *must* be a function or multimethod.

       (fn-symbol even?) => '<even?>
       (fn-symbol (fn [])) => '<fn>
       (fn-symbol (fn name [])) => '<name>
       (let [foo (fn [])] (fn-symbol foo)) => '<foo>

    See [[elaborate-fn-symbol]] for the gory details.
"
  [f]
  (elaborate-fn-symbol f))

(defn fn-string
  "`str` applied to the result of [[fn-symbol]]."
  [f]
  (str (fn-symbol f)))


(defn- better-aliases [x aliases]
  (specter/transform (specter/walker translatable?)
                     translate
                     x))

(defn- better-function-names [x anonymous-names]
  (specter/transform (specter/walker type/extended-fn?)
                     #(elaborate-fn-symbol % *function-elaborations* anonymous-names)
                     x))

(defn value
  "Like [[value-string]], but the final step of converting the value into
   a string is omitted. Note that this means functions are replaced by
   symbols."
  [x]
  (cond (translatable? x)
        (translate x)

        (type/extended-fn? x)
        (fn-symbol x)

        (coll? x)
        (let [anonymous-names (atom {})]
          (-> x
              (better-aliases (deref *translations*))
              (better-function-names anonymous-names)))

        :else
        x))

(defn value-string
  "Except for special values, converts `x` into a string as with `pr-str`.
   Exceptions (which apply anywhere within collections):

   * If a value was given an alternate name in [[with-translations]] or [[instead-of]],
     that alternate is used.

   * Functions and multimethods are given better names as per [[fn-symbol]].

   Examples:

          (value-string even?) => \"<even?>\"
          (value-string {1 {2 [even? odd?]}}) => \"{1 {2 [<even?> <odd?>]}}\"

          (instead-of even? 'not-odd)
          (value-string {1 {2 [even? odd?]}}) => \"{1 {2 [not-odd <odd?>]}}\"

          (def generator (fn [x] (fn [y] (+ x y))))
          (def add2 (generator 2))
          (def add3 (generator 3))
          (value-string [add2 add3 add3 add2]) => \"[<fn> <fn-2> <fn-2> <fn>]\"

          (def add4 (rename (generator 4) 'add4))
          (def add5 (rename (generator 4) 'add5))
          (value-string [add4 add5 add5 add4]) => \"[<add4> <add5> <add5> <add4>]\"
"
  [x]
  (pr-str (value x)))
