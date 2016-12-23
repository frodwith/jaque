(ns jaque.noun.pack
  (:refer-clojure :exclude [atom inc dec zero? cat])
  (:require [jaque.constants :refer :all]
            [jaque.noun.math :refer :all]
            [jaque.noun.bits :refer :all]
            [jaque.noun.read :refer :all]
            [jaque.noun.box  :refer :all]
            [jaque.error     :as e])
  (:import (jaque.noun Atom Cell)))

(defn rub [^Atom a ^Atom b]
  (let [m (add a (met a0 b))
        x (loop [x a]
          (if-not (zero? (cut a0 x a1 b))
            x
          (let [y (inc x)]
          (if& (gth x m)
            (e/exit)
          (recur y)))))]
  (if (= x a)
    (cell a1 a0)
  (let [c (sub x a)
        d (inc x)
        x (dec c)
        y (bex x)
        z (cut a0 d x b)
        e (add y z)
        w (add c c)
        p (add w e)
        z (add d x)
        q (cut a0 z e b)]
  (cell p q)))))

(defn mat [a]
  (if (zero? a)
    (cell a1 a1)
  (let [b (met a0 a)
        c (met a0 b)
        u (dec c)
        v (add c c)
        w (bex c)
        x (end a0 u b)
        y (lsh a0 u a)
        z (mix x y)
        p (add v b)
        q (cat a0 w z)]
  (cell p q))))

(defn cue [a]
  (((fn $ [b m]
     (if (zero? (cut a0 b a1 a))
      (let [c  (rub (inc b) a)
            qc (tail c)]
      [(inc (head c)) qc (assoc! m b qc)])
    (let [c (add a2 b)]
    (if (zero? (cut a0 (inc b) a1 a))
      (let [[pu qu ru] ($ c m)
            [pv qv rv] ($ (add pu c) ru)
            w          (cell qu qv)]
      [(add a2 (add pu pv)) w (assoc! rv b w)])
    (let [d   (rub c a)
          got (get m (tail d))]
    (if (nil? got)
      (e/exit)
    [(add a2 (head d)) got m]))))))
    a0 (transient {}))
   1))

(defn jam [a]
  (((fn $ [a b m]
    (let [c (m a)]
    (if (nil? c)
      (let [m (assoc! m a b)]
      (if (atom? a)
        (let [^Atom a a
              d (mat a)]
        [(inc (head d)) (lsh a0 a1 (tail d)) m])
      (let [b          (add a2 b)
            ^Cell a    a
            [pd qd rd] ($ (head a) b m)
            [pe qe re] ($ (tail a) (add b pd) rd)]
      [(add a2 (add pd pe))
       (mix a1 (lsh a0 a2 (cat a0 qd qe)))
       re])))
    (let [^Atom c c]
    (if (and (atom? a) (<= (.met ^Atom a 0) (.met c 0)))
      (let [d (mat a)]
      [(inc (head d)) (lsh a0 a1 (tail d)) m])
    (let [d (mat c)]
    [(add a2 (head d))
     (mix a3 (lsh a0 a2 (tail d)))
     m]))))))
    a a0 (transient {}))
   1))
