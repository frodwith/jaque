(ns jaque.jets.bit-logic
  (:import (jaque.noun Atom))
  (:require [jaque.noun :refer [a0]])
  (:use [jaque.jets]))

(defj mix [a b]
  (let [w   5
        lna (.met a w)
        lnb (.met b w)]
    (if (and (= 0 lna) (= 0 lnb))
      a0
      (let [len (Math/max lna lnb)
            sal (int-array len)
            bw  (.words b)]
        (Atom/chop w 0 lna 0 sal a)
        (loop [i 0]
          (when (< i lnb)
            (aset sal i (bit-xor (aget sal i) (aget bw i)))
            (recur (inc i))))
        (Atom/malt sal)))))
