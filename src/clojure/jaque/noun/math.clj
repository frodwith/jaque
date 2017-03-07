(ns jaque.noun.math
  (:refer-clojure :exclude [zero? inc dec])
  (:require [jaque.constants :refer :all]
            [jaque.error :as e]
            [jaque.noun.core :refer [export mug lsh]]
            [jaque.noun.read :refer :all])
  (:import jaque.noun.Atom))

(export lth)

(defn gth [^Atom a ^Atom b]
  (if (= 1 (.compareTo a b)) yes no))

(defn add [a b]
  (or (and (instance? Long a)
           (instance? Long b)
           (try (Math/addExact a b)
             (catch ArithmeticException e false)))
      (.add (Atom/coerceAtom a) (Atom/coerceAtom b))))

(defn sub [^Atom a ^Atom b]
  (.sub a b))

(defn inc [^Atom a]
  (.add a a1))

(defn dec [b]
  (cond (instance? Boolean b)
          (if b (e/exit) 0)
        (instance? Long b)
          (if (> b 0)
            (clojure.core/dec b)
            (e/exit))
        :else (.sub ^Atom b a1)))

(defn dor [a b]
  (loop [a a
         b b]
  (if (= a b)
    yes
  (if (atom? a)
    (if (atom? b)
      (lth a b)
    yes)
  (if (atom? b)
    no
  (if (= (head a) (head b))
    (recur (tail a) (tail b))
  (recur (head a) (head b))))))))

(defn gor [a b]
  (let [c (mug a)
        d (mug b)]
    (if (= c d)
      (dor a b)
      (lth c d))))

(defn vor [a b]
  (let [c (mug (mug a))
        d (mug (mug b))]
    (if (= c d)
      (dor a b)
      (lth c d))))

(defn bex [^Atom a]
  (lsh a0 a a1))
