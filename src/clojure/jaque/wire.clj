(ns jaque.wire
  (:require [jaque.atom :refer [lsh rsh mix
                                cut end
                                lot met bex]
                        :as    a])
  (:import jaque.Atom))

(set! *warn-on-reflection* true)

(defn rub "length-decode"
  [^Atom a ^Atom b]
  (let [cl (lot (rsh 0 (.intValue a) b))]
    (if (= 0 cl)
      [1 a/zero]
      (let [ca  (Atom. cl)
            d   (a/add a (a/inc ca))
            dcl (dec cl)
            dca (Atom. dcl)
            e   (a/add (bex dcl) (cut 0 d dcl b))]
        [(a/add (a/add ca ca) e)
         (cut 0 (a/add d dca) e b)]))))

(defn mat "length-encode"
  [^Atom a]
  (if (a/zero? a)
    [1 a/one]
    (let [b  (met 0 a)
          ba (Atom. b)
          c  (met 0 ba)
          s  (+ b c)
          l  (bex c)
          r  (mix (end 0 (dec c) ba)
                  (lsh 0 (dec c) a))
          e (a/cat 0 l r)]
      [s e])))

;(defn cat-bigs [^BigInteger a ^BigInteger b]
;  (.xor (.shiftLeft b (.bitLength a)) a))
;
;(def bitmask (memoize (fn [n] (.subtract ^BigInteger (bex n) one))))
;
;(defn end [n ^BigInteger b] (.and b (bitmask n)))
;(defn cut [off n ^BigInteger b] (end n (.shiftRight b off)))
;
;(defn mat [^BigInteger a]
;  (let [b    (.bitLength a)
;        bb   (biginteger b)
;        c    (.bitLength bb)
;        dc   (dec c)
;        size (+ c c b)
;
;        ; we don't cat-bigs here because leading zeros
;        catd (.xor (.shiftLeft a dc) (end dc bb))
;        enc  (cat-bigs (bex c) catd)]
;    [size enc]))
;
;(defn jam [a]
;  (((fn f [^BigInteger it here where]
;      (let [jatom (fn [m]
;                    (let [[p q] (mat it)]
;                      [(inc p) (.shiftLeft ^BigInteger q 1) m]))
;            there (where it)]
;        (if (nil? there)
;          (let [where (assoc where it here)]
;            (if (atom? it)
;              (jatom where)
;              (let [here          (+ here 2)
;                    [p q]         it
;                    [psz p where] (f p here where)
;                    here          (+ here psz)
;                    [qsz q where] (f q here where)
;                    size          (+ 2 psz qsz)
;                    encoded       (.xor (.shiftLeft ^BigInteger (cat-bigs p q)
;                                                    2)
;                                        one)]
;                [size encoded where])))
;          (if (and (atom? it) (<= (.bitLength it) (.bitLength (biginteger here))))
;            (jatom where)
;            (let [[p q] (mat there)]
;              [(+ 2 p)
;               (.xor (.shiftLeft ^BigInteger q 2) three)
;               where])))))
;    a zero {}) 1))
;
;(defn rub [a ^BigInteger b]
;  (let [c (.getLowestSetBit (.shiftRight b a))]
;    (if (< c 1)
;      [1 zero]
;      (let [d  (+ a c 1)
;            dc (dec c)
;            e  (.add ^BigInteger (bex dc) (cut d dc b))]
;        [(+ c c e) (cut (+ d dc) e b)]))))
;
;(defn cue [^BigInteger a]
;  (((fn f [here where]
;      (if-not (.testBit a here)
;        (let [[p q] (rub (inc here) a)]
;          [(inc p) q (assoc where here q)])
;        (let [there (+ 2 here)]
;          (if (.testBit a (inc here))
;            (let [[p q] (rub there a)]
;              [(+ 2 p) (where q) where])
;            (let [[usz u where] (f there where)
;                  [vsz v where] (f (+ usz there) where)
;                  w             (tuple u v)]
;              [(+ 2 usz vsz) w (assoc where here w)])))))
;    zero {}) 1))
