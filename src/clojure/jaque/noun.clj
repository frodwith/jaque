(ns jaque.noun
  (:refer-clojure :exclude [atom])
  (:require [slingshot.slingshot :refer [throw+]])
  (:import (jaque.noun Atom Cell Noun)
           (clojure.lang BigInt)
           (java.math BigInteger)))

(def atom? (partial instance? Atom))
(def cell? (partial instance? Cell))
(def noun? (partial instance? Noun))

(defn hed [^Cell c] (.p c))
(defn tal [^Cell c] (.q c))

(defn cell ^Cell [& xs]
  ((fn $ [c xs]
     (cond (< c 2) (throw+ {:message "A cell must be at least two things."
                            :count    c
                            :bad-cell xs})
           (= c 2) (Cell. ^Noun (first xs) ^Noun (second xs))
           :else   (Cell. ^Noun (first xs) ^Noun ($ (dec c) (rest xs)))))
   (count xs) xs))

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

(defn noun [v]
  (cond (noun? v)   v
        (integer? v) (atom v)
        (vector? v)  (apply cell (map noun v))
        :else        (throw+ {:message "bad argument to noun"
                              :bad-noun v})))

(doseq [i [0 1 2 3 10]]
  (intern *ns* (symbol (str "a" i)) (atom i)))

(def yes a0)
(def no  a1)

(defn loob [bool]
  (if bool yes no))
