(ns such.f-environment
  (:require [such.environment :as subject])
  (:use midje.sweet))

(fact env
  (subject/env :home) => string?
  (subject/env :does-not-exist) => (throws))

(fact env-nil-ok
  (subject/env-nil-ok :home) => string?
  (subject/env-nil-ok :does-not-exist) => nil)


