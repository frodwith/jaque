(ns jaque.noun.nlr
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.noun.read :refer :all]
            [jaque.noun.core :refer [export gor]]
            [jaque.noun.box  :refer :all]
            [jaque.constants :refer :all])
  (:import jaque.noun.Noun))

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

(defn nlr-seq [^Noun n]
  (let [[n l r] (trel-seq n)]
  (if (or (nil? n) (zero? n))
    nil
  (concat (nlr-seq l) (cons n (nlr-seq r))))))
