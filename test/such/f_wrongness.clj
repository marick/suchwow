(ns such.f-wrongness (:require [such.wrongness :as subject])
  (:use midje.sweet))

(fact 
  (try 
    (subject/boom! "wow!")
    "fail" => true
  (catch Exception ex
    (.getMessage ex) => "wow!"))

  (try 
    (subject/boom! "wow! %s" 5)
    "fail" => true
  (catch Exception ex
    (.getMessage ex) => "wow! 5"))

  (try 
    (subject/boom! NumberFormatException "wow! %s" 5)
    "fail" => true
  (catch NumberFormatException ex
    (.getMessage ex) => "wow! 5")))


