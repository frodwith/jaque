(ns jaque.util
  (:refer-clojure :exclude [inc met atom])
  (:require [jaque.jets.math :refer [inc lth]]
            [jaque.jets.bit-surgery :refer [met cut]]
            [jaque.noun :refer :all])
  (:import (jaque.noun Atom)))

(defn bits [^Atom a]
  (let [len (met a0 a)
        f   (fn bits-in [i]
              (when (lth i len)
                (lazy-seq (cons (cut a0 i a1 a)
                                (bits-in (inc i))))))]
    (f a0)))

(defn cell-list [c]
  (when (cell? c)
    (lazy-seq (cons (hed c) (cell-list (tal c))))))
