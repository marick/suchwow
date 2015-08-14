(ns such.doc
  "Links to, and support for, online documentation."
  (:require [clojure.java.browse :as browse]
            [clojure.pprint :refer [cl-format]]
            [clojure.string :as str]))

(def ^:private api-namespaces (atom []))

(def ^:private api-doc-template
  "Open this library's API documentation in a browser.
      
   To auto-load this and all such functions into the repl, put 
   the following in `project.clj`:
      
        :repl-options {:init (do (require '%s 'etc 'etc)
                                 ;; List available api docs on repl startup:
                                 (such.doc/apis))}
")

;; This is a macro to let us capture the calling namespace.
(defmacro api-url!
  "This defines an `api` function that jumps to online documentation. 
   Put something like the following in a `some-library.doc` namespace:
   
        (ns some-library.doc
          \"Functions that jump to online documentation.\"
          (:require such.doc))
        
       (such.doc/api-url! \"http://marick.github.io/structural-typing/\")
   
   A client of `some-library` can put the following in `project.clj`:
   
        :repl-options {:init (do (require 'some-library.doc 'etc 'etc)
                                 ;; List available api docs on repl startup:
                                 (such.doc/apis))}

   Thereafter, the documentation is available in the repl via
   
        (some-library.doc/api)
"
  [url]
  (do 
    (swap! api-namespaces #(-> %
                               (conj (ns-name *ns*))
                               set
                               vec
                               sort))

    (let [var (intern *ns* 'api (fn [] (browse/browse-url url)))]
      (alter-meta! var assoc :doc (format api-doc-template (ns-name *ns*))))))


(api-url! "http://marick.github.io/suchwow/")

(defn apis
  "List (to stdout) all APIs that provide in-repl documentation via
   `(some-library.doc/api)`."
  []
  (println "The following namespaces have an `api` function to take you to API docs:")
  (cl-format true "   ~{~<~%   ~1,80:; ~S~>~^,~}~%" @api-namespaces)
  (println "At any time, see available namespaces with `(such.api/apis)`")
  (println))

