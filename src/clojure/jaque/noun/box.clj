(ns jaque.noun.box
  (:refer-clojure :exclude [atom])
  (:require [jaque.noun.read :refer [atom? noun?]]
            [jaque.jets.nlr :refer [by-put]]
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
        (char? a)    (Atom/fromLong (int a))
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

(defn map->nlr [m]
  (reduce #(by-put %1 (noun (%2 0)) (noun (%2 1)))
          (atom 0) m))

(defn seq->it [s]
  (reduce #(Cell. (noun %2) (noun %1)) (atom 0) (reverse s)))

(defn string->cord ^Atom [^String s]
  (Atom/fromByteArray (.getBytes s "UTF-8") Atom/LITTLE_ENDIAN))

(defn noun [v]
  (cond (noun? v)    v
        (integer? v) (atom v)
        (char? v)    (atom (int v))
        (keyword? v) (string->cord (name v))
        (vector? v)  (apply cell (map noun v))
        (map? v)     (map->nlr v)
        (string? v)  (seq->it (seq v))
        (seq? v)     (seq->it v)
        :else        (throw+ {:message "bad argument to noun"
                              :bad-noun v})))
