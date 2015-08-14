(ns such.api
  "Links to, and support for, online documentation."
  (:require [clojure.java.browse :as browse]
            [clojure.pprint :refer [cl-format]]
            [clojure.string :as str]))

(def ^:private api-namespaces (atom []))

(def ^:private open-doc-template
  "Open this library's API documentation in a browser.
      
   To auto-load this and all such functions into the repl, put 
   the following in `project.clj`:
      
        :repl-options {:init (do (require '%s 'etc 'etc)
                                 ;; List available api docs on repl startup:
                                 (such.api/apis))}
")

;; This is a macro to let us capture the calling namespace.
(defmacro api-url!
  "This defines
   an `open` function that links to online documentation. 
   Put something like the following in a `some-library.api` namespace:
   
        (ns structural-typing.api
          \"Links to online documentation.\"
          (:require such.api))
        
       (such.api/api-url! \"http://marick.github.io/structural-typing/\")
   
   A client of `some-library` can put the following in `project.clj`:
   
        :repl-options {:init (do (require 'some-library.api 'etc 'etc)
                                 ;; List available api docs on repl startup:
                                 (such.api/apis))}

   Thereafter, the documentation is available in the repl via
   
        (some-library.api/open)
"
  [url]
  (do 
    (swap! api-namespaces #(-> %
                               (conj (ns-name *ns*))
                               set
                               vec
                               sort))

    (let [var (intern *ns* 'open (fn [] (browse/browse-url url)))]
      (alter-meta! var assoc :doc (format open-doc-template (ns-name *ns*))))))


(api-url! "http://marick.github.io/suchwow/")

(defn apis
  "List (to stdout) all APIs that provide in-repl documentation via
   `(some-library.api/open)`."
  []
  (println "The following namespaces have an `open` function to take you to  docs:")
  (cl-format true "   ~{~<~%   ~1,80:; ~S~>~^,~}~%" @api-namespaces)
  (println "At any time, see available namespaces with `(such.api/apis)`")
  (println))

