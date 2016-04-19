(ns jaque.jets.packing
  (:refer-clojure :exclude [inc dec cat atom])
  (:use (jaque.jets bit-logic bit-surgery math))
  (:require [jaque.error :as e]
            [jaque.jets :refer [defj]]
            [jaque.noun :refer [cell atom atom?]])
  (:import (jaque.noun Atom Cell)))

(defj rub* [a b]
  (let [m (add a (met (atom 0) b))
        x (loop [x a]
            (if (.isZero (cut (atom 0) x (atom 1) b))
              (let [y (inc x)]
                (if (gth x m)
                  (e/exit)
                  (recur y)))
              x))]
    (if (= x a)
      (cell (atom 1) (atom 0))
      (let [c (sub x a)
            d (inc x)
            x (dec c)
            y (bex x)
            z (cut (atom 0) d x b)
            e (add y z)
            w (add c c)
            p (add w e)
            z (add d x)
            q (cut (atom 0) z e b)]
        (cell p q)))))

(defj mat* [a]
  (if (.isZero a)
    (cell (atom 1) (atom 1))
    (let [b (met (atom 0) a)
          c (met (atom 0) b)
          u (dec c)
          v (add c c)
          w (bex c)
          x (end (atom 0) u b)
          y (lsh (atom 0) u a)
          z (mix x y)
          p (add v b)
          q (cat (atom 0) w z)]
      (cell p q))))

(defj cue [a]
  (let [go (fn $ [b m]
             (if (.isZero (cut (atom 0) b (atom 1) a))
               (let [c  (rub (inc b) a)
                     qc (.q c)] 
                 [(inc (.p c)) qc (assoc! m b qc)])
               (let [c (add (atom 2) b)]
                 (if (.isZero (cut (atom 0) (inc b) (atom 1) a))
                   (let [[pu qu ru] ($ c m)
                         [pv qv rv] ($ (add pu c) ru)
                         w          (cell qu qv)]
                     [(add (atom 2) (add pu pv)) w (assoc! rv b w)])
                   (let [d (rub c a)
                         got     (m (.longValue ^Atom (.q d)))]
                     (if (nil? got)
                       (e/exit)
                       [(add (atom 2) (.p d)) got m]))))))
        [p q r] (go (atom 0) (transient {}))]
    q))

(defn jam "pack"
  [a]
  (let [go (fn $ [a b m]
             (let [c (m a)]
               (if (nil? c)
                 (let [m (assoc! m a b)]
                   (if (atom? a)
                     (let [^Atom a a
                           d (mat a)]
                       [(inc (.p d)) (lsh (atom 0) (atom 1) (.q d)) m])
                     (let [b          (add (atom 2) b)
                           ^Cell a    a
                           [pd qd rd] ($ (.p a) b m)
                           [pe qe re] ($ (.q a) (add b pd) rd)]
                       [(add (atom 2) (add pd pe))
                        (mix (atom 1) (lsh (atom 0) (atom 2)
                                           (cat (atom 0) qd qe)))
                        re])))
                 (let [^Atom c c]
                   (if (and (atom? a) (<= (.met ^Atom a 0) (.met c 0)))
                     (let [d (mat a)]
                       [(inc (.p d)) (lsh (atom 0) (atom 1) (.q d)) m])
                     (let [d (mat c)]
                       [(add (atom 2) (.p d)) 
                        (mix (atom 3) (lsh (atom 0) (atom 2) (.q d))) 
                        m]))))))
        [p q r] (go a (atom 0) (transient {}))]
    q))
