(ns such.environment
  (:require environ.core)
  (:require [such.wrongness :as !]))

(defn env
  "Select a keyword `key` from the environment. The result is a string.
   It is an error for the environment lookup to return `nil`. See [[env-nil-ok]].

   Key handling and where environment values come from is per 
   (weavejester/environ)[https://github.com/weavejester/environ]:

        (env :home)          ; lowercased
        (env :database-url)  ; would match `DATABASE_URL`
"
  [key]
  (if-let [result (environ.core/env key)]
    result
    (!/boom! "%s is not in the environment." key)))

(defn env-nil-ok
  "Select a keyword `key' from the environment, returning a string.
   The result may be `nil`."
  [key]
  (environ.core/env key))
