(ns jaque.math
  (:require [jaque.error :as e]
            [jaque.constants :refer [a0 a1 a2 a3]])
  (:import (jaque.noun Atom)))

(declare lsh)

(defn bex [^Atom a]
  (lsh a0 a a1))

(defn bloq [^Atom a]
  (let [v (.intValue a)]
    (if (or (< v 0) (> v 32))
      (e/fail)
      v)))

(defn cap [^Atom a]
  (let [m (.met a 0)]
    (cond (< m 2)
            (e/exit)
          (.bit a (- m 2))
            a3
          :else
            a2)))

(defn lsh [^Atom a ^Atom b ^Atom c]
  (when-not (.isCat b) (e/fail))
  (let [a   (bloq a)
        b   (.intValue b)
        len (.met c a)]
    (if (= 0 len)
      a0
      (let [lus (+ b len)]
        (when-not (>= lus len) (e/fail))
        (let [sal (Atom/slaq a lus)]
          (Atom/chop a 0 len b sal c)
          (Atom/malt sal))))))

(defn mas [^Atom a]
  (let [b (.met a 0)]
    (if (< b 2)
      (e/exit)
      (let [c (bex (Atom/fromLong (- b 1)))
            d (bex (Atom/fromLong (- b 2)))
            e (.sub a c)]
        (Atom/con e d)))))
