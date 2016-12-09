(ns jaque.noun.motes
  (:require [jaque.noun.box :refer [string->cord]]))

(defn- make-mote-def [sym]
  (let [bare (name sym)
        cord (string->cord bare)
        sym  (symbol (apply str (list "%" bare)))]
    `(def ~sym ~cord)))

(defmacro defmote [& ms]
  `(do ~@(map make-mote-def ms)))
