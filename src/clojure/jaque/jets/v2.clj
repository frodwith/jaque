(ns jaque.jets.v2
  (:refer-clojure :exclude [atom zero? cat inc dec])
  (:require [jaque.noun.motes :refer [defmote]]
            [jaque.noun.read :refer [if& atom? zero? lark head tail lth?]]
            [jaque.jets.nlr :as nlr]
            [jaque.noun.box :refer :all]
            [jaque.constants :refer :all]
            [jaque.jets.jet :refer [defjet]]
            [jaque.error :as e]
            [jaque.math :as math])
  (:import (jaque.noun Atom Noun)
           (java.util Arrays)
           (java.security MessageDigest)))

;; All jets in volume 2 of hoon.hoon (yes, that is most of them) should be
;; defined here. The defgate macros below are provided as a convenience for
;; jetting a gate at axis 2 of a core labeled starting with its name and with
;; the hoon core as the parent (e.g. cap goes to [cap hoon mood k151])
;; with arbitrary, one, cell, or trel arguments respectively.

(defmacro defgate [n & mean-on]
  `(defjet ~n [~n hoon mood k151] - ~@mean-on))

(defmacro defg1 [n & args-on]
  `(defgate ~n [+<] ~@args-on))

(defmacro defg2 [n & args-on]
  `(defgate ~n [+<- +<+] ~@args-on))

(defmacro defg3 [n & args-on]
  `(defgate ~n [+<- +<+< +<+>] ~args-on))

(def bloq math/bloq)

(defg1 bex [a]
  (math/bex a))

(defg2 met [a b]
  (let [a (bloq a)
        i (.met b a)]
    (Atom/fromLong i)))

(defg3 cat [^Atom a ^Atom b ^Atom c]
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

(defgate cut [+<- +<+<- +<+<+ +<+>] [^Atom a ^Atom b ^Atom c ^Atom d]
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

(defg3 end [^Atom a ^Atom b ^Atom c]
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

(defg3 lsh [a b c]
  (math/lsh a b c))

(defg3 rsh [^Atom a ^Atom b ^Atom c]
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

(defg1 cap [a]
  (math/cap a))

(defg1 mas [a]
  (math/mas a))

(defg2 add [^Atom a ^Atom b]
  (.add a b))

(defg2 sub [^Atom a ^Atom b]
  (.sub a b))

(defg1 inc [^Atom a]
  (add a a1))

(defg1 dec [^Atom b]
  (sub b a1))

(defg2 con [^Atom a ^Atom b]
  (Atom/con a b))

(defg2 mix [^Atom a ^Atom b]
  (Atom/mix a b))

(defg2 lth [a b]
  (if (lth? a b) yes no))

(defg2 gth [^Atom a ^Atom b]
  (if (= 1 (.compareTo a b)) yes no))

(defg2 rub [^Atom a ^Atom b]
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

(defg1 mat [a]
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

(defg1 cue [a]
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

(defg1 jam [a]
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

(defg1 mug [a]
  (jaque.noun.read/mug a))

(defg2 shay [^Atom len ^Atom ruz]
  (let [dig (MessageDigest/getInstance "SHA-256")
        len (.intValue len)
        byt (.toByteArray ruz Atom/LITTLE_ENDIAN)
        byt (Arrays/copyOfRange byt 0 len)
        h   (.digest dig byt)]
    (Atom/fromByteArray h Atom/LITTLE_ENDIAN)))

(defg1 shax [ruz]
  (shay (met a3 ruz) ruz))

(defg2 shas [sal ruz]
  (shax (mix sal (shax ruz))))

(defg2 shaf [sal ruz]
  (let [haz (shas sal ruz)]
    (mix (end a7 a1 haz) (rsh a7 a1 haz))))

(defmote mash sham)

(defg1 sham [yux]
  (if (atom? yux)
    (shaf %mash yux)
    (shaf %sham (jam yux))))

(defg2 dor [a b]
  (nlr/dor a b))

(defg2 gor [a b]
  (nlr/gor a b))

(defg2 vor [a b]
  (nlr/vor a b))

(defjet by-get [get by hoon mood k151] - [+>+< +<] [a b]
  (nlr/by-get a b))

(defjet by-put [put by hoon mood k151] - [+>+< +<- +<+] [a b c]
  (nlr/by-put a b c))
