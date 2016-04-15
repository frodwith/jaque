(ns jaque.jets.bit-surgery
  (:refer-clojure :exclude [cat])
  (:import [jaque.noun Atom])
  (:use [jaque.jets]))

(set! *warn-on-reflection* true)

(declare lsh)

(defj bex [n] (lsh Atom/ZERO n Atom/ONE))
(defj cat [a b c]
  (let [a   (bloq a)
        lew (.met b a)
        ler (.met c a)
        all (+ lew ler)]
    (if (= 0 all)
      Atom/ZERO
      (let [sal (Atom/slaq a all)]
        (Atom/chop a 0 lew 0   sal b)
        (Atom/chop a 0 ler lew sal c)
        (Atom/malt sal)))))

(defj cut [a b c d]
  (let [a   (bloq a)
        len (.met d a)]
    (if-not (.isCat b)
      Atom/ZERO
      (let [b (.intValue b)
            c (if (.isCat c) (.intValue c) (Integer/MAX_VALUE))]
        (if (or (= 0 c) (>= b len))
          Atom/ZERO
          (let [c (if (> (+ b c) len) (- len b) c)]
            (if (and (= 0 b) (= c len))
              d
              (let [sal (Atom/slaq a c)]
                (Atom/chop a b c 0 sal d)
                (Atom/malt sal)))))))))

(defj end [a b c]
  (if-not (.isCat b)
    c
    (if (.isZero b)
      Atom/ZERO
      (let [a   (bloq a)
            len (.met c a )
            b   (.intValue b)]
        (if (>= b len)
          c
          (let [sal (Atom/slaq a b)]
            (Atom/chop a 0 b 0 sal c)
            (Atom/malt sal)))))))

(defj lsh [a b c]
  (assert (.isCat b))
  (let [a   (bloq a)
        len (.met c a)]
    (if (= 0 len)
      Atom/ZERO
      (let [lus (+ b len)]
        (assert (>= lus len))
        (let [sal (Atom/slaq a lus)]
          (Atom/chop a 0 len b sal c)
          (Atom/malt sal))))))

(defj met [a b]
  (let [a (bloq a)
        i (.met b a)]
    (Atom/fromLong i)))

(defj rsh [a b c]
  (let [a   (bloq a)
        len (.met c a)]
    (if (>= b len)
      Atom/ZERO
      (let [hep (- len b)
            sal (Atom/slaq a hep)]
        (Atom/chop a b hep 0 sal c)
        (Atom/malt sal)))))
