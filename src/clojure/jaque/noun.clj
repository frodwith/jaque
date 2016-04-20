(ns jaque.noun
  (:refer-clojure :exclude [atom])
  (:require [slingshot.slingshot :refer [throw+]])
  (:import (jaque.noun Atom)
           (clojure.lang BigInt)
           (java.math BigInteger)))

(deftype Cell [p q]
  Object
  (equals [a b] (and (instance? Cell b)
                     (let [^Cell b b]
                       (and (= (.p a) (.p b))
                            (= (.q a) (.q b))))))
  (hashCode [c]
    (+ (* 37 (+ 37 (.hashCode (.p c))))
       (.hashCode (.q c))))

  (toString [c] (format "[%s %s]" (.p c) (.q c))))

(def atom? (partial instance? Atom))
(def cell? (partial instance? Cell))
(defn noun? [a] (or (atom? a) (cell? a)))

(defn cell ^Cell [& xs]
  ((fn $ [c xs]
     (cond (< c 2) (throw+ {:message "A cell must be at least two things."
                            :bad-cell xs})
           (= c 2) (->Cell (first xs) (second xs))
           :else   (->Cell (first xs) ($ (dec c) (rest xs)))))
   (count xs) xs))

(defn atom ^Atom [a]
  (cond (atom? a)    a
        (integer? a) (cond (instance? BigInteger a)
                             (Atom/fromBigEndian (.toByteArray ^BigInteger a))
                           (instance? BigInt a)
                             (let [^BigInt a a]
                               (if (nil? (.bipart a))
                                 (atom (.lpart a))
                                 (atom (.bipart a))))
                           :else (Atom/fromLong a))
        (string? a)  (Atom/fromString a)
        :else        (throw+ {:message "atom must be passed an integer or a string"
                              :bad-atom a})))

(doseq [i [0 1 2 3 10]]
  (intern *ns* (symbol (str "a" i)) (atom i)))
