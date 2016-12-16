(ns jaque.jets.nlr
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.noun.read :refer :all]
            [jaque.constants :refer :all])
  (:import (jaque.noun Atom Cell)))

;; core functions for dealing with nlrs, resolving a circular dependency issue
;; similar to jets.math

(defn dor [a b]
  (loop [a a
         b b]
  (if (= a b)
    yes
  (if (atom? a)
    (if (atom? b)
      (if (lth? a b) yes no)
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
      (if (lth? c d) yes no))))

(defn vor [a b]
  (let [c (mug (mug a))
        d (mug (mug b))]
    (if (= c d)
      (dor a b)
      (if (lth? c d) yes no))))

(defn by-get [a b]
  (loop [a a]
  (if (zero? a)
    a0
  (if (= b (lark -< a))
    (Cell. a0 (lark -> a))
  (recur (if& (gor b (lark -< a))
           (lark +< a)
         (lark +> a)))))))

(defn by-put [a b c]
  (if (zero? a)
    (Cell. (Cell. b c) (Cell. a0 a0))
  (if (= b (lark -< a))
    (if (= c (lark -> a))
      a
    (Cell. (Cell. b c) (Cell. (lark +< a) (lark +> a))))
  (if& (gor b (lark -< a))
    (let [d (by-put (lark +< a) b c)]
    (if& (vor (lark -< a) (lark -< d))
      (Cell. (head a) (Cell. d (lark +> a)))
    (Cell. (head d)
    (Cell. (lark +< d)
    (Cell. (head a)
    (Cell. (lark +> d) (lark +> a)))))))
  (let [d (by-put (lark +> a) b c)]
  (if& (vor (lark -< a) (lark -< d))
    (Cell. (head a) (Cell. (lark +< a) d))
  (Cell. (head d)
  (Cell. (Cell. (head a) (Cell. (lark +< a) (lark +< d)))
  (lark +> d)))))))))
