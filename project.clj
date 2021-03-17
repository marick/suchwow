(defproject marick/suchwow "6.0.3"
  :description "Such functions! Such doc strings! Much utility!"
  :url "https://github.com/marick/suchwow"
  :pedantic? :warn
  :license {:name "The Unlicense"
            :url "http://unlicense.org/"
            :distribution :repo}

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [potemkin "0.4.5" :exclusions [org.clojure/clojure]]
                 [com.rpl/specter "1.1.3" :exclusions [org.clojure/clojure org.clojure/clojurescript]]
                 [environ "1.2.0" :exclusions [org.clojure/clojure]]
                 [commons-codec/commons-codec "1.15"]]

  :repl-options {:init (do (require 'such.doc)
                           (such.doc/apis))}

  :profiles {:dev {:dependencies [[midje "1.9.10" :exclusions [org.clojure/clojure]]
                                  [org.clojure/math.combinatorics "0.1.6"]
                                  [org.clojure/data.json "1.1.0"]
                                  ;; Including compojure so that `lein ancient` will
                                  ;; tell us to upgrade, which might alert us that
                                  ;; compojure now depends on a more-modern version of
                                  ;; commons-codec.
                                  [marick/structural-typing "2.0.5" :exclusions [marick/suchwow]]
                                  [compojure "1.6.2" :exclusions [org.clojure/clojure]]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}}

  :plugins [[lein-midje "3.2.1"]
            [codox "0.8.11"]]

  :codox {:src-dir-uri "https://github.com/marick/suchwow/blob/master/"
          :src-linenum-anchor-prefix "L"
          :output-dir "/var/tmp/suchwow-doc"
          :defaults {:doc/format :markdown}}

  :aliases {"compatibility" ["with-profile" "+1.7:+1.8:+1.9" "midje" ":config" ".compatibility-test-config"]
            "travis" ["with-profile" "+1.7:+1.8:+1.9" "midje"]}

  ;; For Clojure snapshots
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :deploy-repositories [["releases" :clojars]])
