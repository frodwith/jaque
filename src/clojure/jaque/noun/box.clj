(ns jaque.noun.box
  (:refer-clojure :exclude [atom])
  (:require [jaque.noun.read :refer [atom? noun?]]
            [slingshot.slingshot :refer [throw+]])
  (:import (jaque.noun Atom Cell Noun)
           (clojure.lang BigInt)
           (java.math BigInteger)))

(defn atom ^Atom [a]
  (cond (atom? a)    a
        (integer? a) (cond (instance? BigInteger a)
                             (Atom/fromByteArray (.toByteArray ^BigInteger a) Atom/BIG_ENDIAN)
                           (instance? BigInt a)
                             (let [^BigInt a a]
                               (if (nil? (.bipart a))
                                 (atom (.lpart a))
                                 (atom (.bipart a))))
                           :else (Atom/fromLong a))
        (string? a)  (Atom/fromString a)
        :else        (throw+ {:message "atom must be passed an integer or a string"
                              :bad-atom a})))

(defn cell ^Cell [& xs]
  ((fn $ [c xs]
     (cond (< c 2) (throw+ {:message "A cell must be at least two things."
                            :count    c
                            :bad-cell xs})
           (= c 2) (Cell. ^Noun (first xs) ^Noun (second xs))
           :else   (Cell. ^Noun (first xs) ^Noun ($ (dec c) (rest xs)))))
   (count xs) xs))

(defn noun [v]
  (cond (noun? v)   v
        (integer? v) (atom v)
        (vector? v)  (apply cell (map noun v))
        :else        (throw+ {:message "bad argument to noun"
                              :bad-noun v})))

(defn string->cord ^Atom [^String s]
  (Atom/fromByteArray (.getBytes s "UTF-8") Atom/LITTLE_ENDIAN))
