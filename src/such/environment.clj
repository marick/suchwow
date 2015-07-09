(ns such.environment
  (:require environ.core)
  (:require [such.wrongness :as !]))

(defn env
  "Select a keyword `key` from the environment. The result is a string.
   Throws an error if the environment lookup returns `nil`. See [[env-nil-ok]].

   Environment variables are handled as described in
   [weavejester/environ](https://github.com/weavejester/environ):

        (env :home)          ; lowercased
        (env :database-url)  ; would match `DATABASE_URL`

   Also see that documentation for where environment variables can be set.
"
  [key]
  (if-let [result (environ.core/env key)]
    result
    (!/boom! "%s is not in the environment." key)))

(defn env-nil-ok
  "Select a keyword `key` from the environment, returning a string.
   The result may be `nil`."
  [key]
  (environ.core/env key))
