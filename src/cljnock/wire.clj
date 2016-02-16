(ns cljnock.wire
  (:require [tuples.core :refer [tuple]])
  (:require [cljnock.core :refer [atom?]]))

(set! *warn-on-reflection* true)

(def zero  (biginteger 0))
(def one   (biginteger 1))
(def three (biginteger 3))

(defn cat-bigs [^BigInteger a ^BigInteger b]
  (.xor (.shiftLeft b (.bitLength a)) a))

(def bex (memoize (fn [n] (.shiftLeft ^BigInteger one n))))
(def bitmask (memoize (fn [n] (.subtract ^BigInteger (bex n) one))))

(defn end [n ^BigInteger b] (.and b (bitmask n)))
(defn cut [off n ^BigInteger b] (end n (.shiftRight b off)))

(defn mat [^BigInteger a]
  (let [b    (.bitLength a)
        bb   (biginteger b)
        c    (.bitLength bb)
        dc   (dec c)
        size (+ c c b)

        ; we don't cat-bigs here because leading zeros
        catd (.xor (.shiftLeft a dc) (end dc bb))
        enc  (cat-bigs (bex c) catd)]
    [size enc]))

(defn jam [a]
  (((fn f [^BigInteger it here where]
      (let [jatom (fn [m]
                    (let [[p q] (mat it)]
                      [(inc p) (.shiftLeft ^BigInteger q 1) m]))
            there (where it)]
        (if (nil? there)
          (let [where (assoc where it here)]
            (if (atom? it)
              (jatom where)
              (let [here          (+ here 2)
                    [p q]         it
                    [psz p where] (f p here where)
                    here          (+ here psz)
                    [qsz q where] (f q here where)
                    size          (+ 2 psz qsz)
                    encoded       (.xor (.shiftLeft ^BigInteger (cat-bigs p q)
                                                    2)
                                        one)]
                [size encoded where])))
          (if (and (atom? it) (<= (.bitLength it) (.bitLength (biginteger here))))
            (jatom where)
            (let [[p q] (mat there)]
              [(+ 2 p)
               (.xor (.shiftLeft ^BigInteger q 2) three)
               where])))))
    a zero {}) 1))

(defn rub [a ^BigInteger b]
  (let [c (.getLowestSetBit (.shiftRight b a))]
    (if (< c 1)
      [1 zero]
      (let [d  (+ a c 1)
            dc (dec c)
            e  (.add ^BigInteger (bex dc) (cut d dc b))]
        [(+ c c e) (cut (+ d dc) e b)]))))

(defn cue [^BigInteger a]
  (((fn f [here where]
      (if-not (.testBit a here)
        (let [[p q] (rub (inc here) a)]
          [(inc p) q (assoc where here q)])
        (let [there (+ 2 here)]
          (if (.testBit a (inc here))
            (let [[p q] (rub there a)]
              [(+ 2 p) (where q) where])
            (let [[usz u where] (f there where)
                  [vsz v where] (f (+ usz there) where)
                  w             (tuple u v)]
              [(+ 2 usz vsz) w (assoc where here w)])))))
    zero {}) 1))
