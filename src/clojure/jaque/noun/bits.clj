(ns jaque.noun.bits
  (:refer-clojure :exclude [zero? cat])
  (:require [jaque.error :as e]
            [jaque.constants :refer :all]
            [jaque.noun.read :refer [zero?]]
            [jaque.noun.core :refer [bex bloq export]])
  (:import jaque.noun.Atom))

(export cap mas met lsh)

(defn cat [^Atom a ^Atom b ^Atom c]
  (let [a   (bloq a)
        lew (.met b a)
        ler (.met c a)
        all (+ lew ler)]
  (if (= 0 all)
    a0
  (let [sal (Atom/slaq a all)]
    (Atom/chop a 0 lew 0   sal b)
    (Atom/chop a 0 ler lew sal c)
    (Atom/malt sal)))))

(defn cut [^Atom a ^Atom b ^Atom c ^Atom d]
  (let [a   (bloq a)
        len (.met d a)]
  (if-not (.isCat b)
    a0
  (let [b (.intValue b)
        c (if (.isCat c) (.intValue c) (Integer/MAX_VALUE))]
  (if (or (= 0 c) (>= b len))
    a0
  (let [c (if (> (+ b c) len) (- len b) c)]
  (if (and (= 0 b) (= c len))
    d
  (let [sal (Atom/slaq a c)]
    (Atom/chop a b c 0 sal d)
    (Atom/malt sal)))))))))

(defn end [^Atom a ^Atom b ^Atom c]
  (if-not (.isCat b)
    c
  (if (zero? b)
    a0
  (let [a   (bloq a)
        len (.met c a )
        b   (.intValue b)]
  (if (>= b len)
    c
  (let [sal (Atom/slaq a b)]
    (Atom/chop a 0 b 0 sal c)
    (Atom/malt sal)))))))

(defn rsh [^Atom a ^Atom b ^Atom c]
  (when-not (.isCat b) (e/fail))
  (let [a   (bloq a)
        len (.met c a)
        b   (.intValue b)]
  (if (>= b len)
    a0
  (let [hep (- len b)
        sal (Atom/slaq a hep)]
    (Atom/chop a b hep 0 sal c)
    (Atom/malt sal)))))

(defn con [^Atom a ^Atom b]
  (Atom/con a b))

(defn mix [^Atom a ^Atom b]
  (Atom/mix a b))
