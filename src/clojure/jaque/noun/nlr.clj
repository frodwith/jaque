(ns jaque.noun.nlr
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.noun.read :refer :all]
            [jaque.noun.core :refer [export gor]]
            [jaque.noun.box  :refer :all]
            [jaque.constants :refer :all])
  (:import (jaque.noun Atom Cell)))

(export by-put)

(defn by-get [a b]
  (loop [a a]
  (if (zero? a)
    a0
  (if (= b (lark -< a))
    (noun [0 (lark -> a)])
  (recur (if& (gor b (lark -< a))
           (lark +< a)
         (lark +> a)))))))

