(ns jaque.jets.map
  (:refer-clojure :exclude [atom])
  (:require [jaque.jets.math :refer [lth]]
            [jaque.jets.hash :refer [mug]]
            [jaque.noun :refer :all])
  (:import (jaque.noun Cell)))

(defn dor [a b]
  (if (= a b)
    true
    (if-not (atom? a)
      (if (atom? b)
        false
        (let [ha (hed a)
              hb (hed b)]
          (if (= ha hb)
            (dor (tal a) (tal b))
            (dor ha hb))))
      (if-not (atom? b)
        true
        (lth a b)))))

(defn gor [a b]
  (let [c (mug a)
        d (mug b)]
    (if (= c d)
      (dor a b)
      (lth c d))))

(defn vor [a b]
  (let [c (mug (mug a))
        d (mug (mug b))]
    (if (= c d)
      (dor a b)
      (lth c d))))

(defn get-by [a b]
  (loop [a a]
    (if-not (cell? a)
      a0
      (let [n (hed a)
            p (hed n)]
      (if (= b p) 
        (Cell. a0 (tal n))
        (recur ((if (gor b p) hed tal) (tal a))))))))

(defn put-by [a b c]
  (if-not (cell? a)
    (Cell. (Cell. b c) (Cell. a0 a0))
    (let [na  (hed a)
          pna (hed na)
          qna (tal na)
          ta  (tal a)
          la  (hed ta)
          ra  (tal ta)]
      (if (= b pna)
        (if (= c qna)
          a
          (Cell. (Cell. b c) (Cell. la ra)))
        (let [g   (gor b pna)
              d   (put-by (if g la ra) b c)
              nd  (hed d)
              pnd (hed nd)
              td  (tal d)
              ld  (hed td)
              rd  (tal td)
              v   (vor pna pnd)]
          (if g
            (if v
              (Cell. na (Cell. d ra))
              (Cell. nd (Cell. ld (Cell. na (Cell. rd ra)))))
            (if v
              (Cell. na (Cell. la d))
              (Cell. nd (Cell. (Cell. na (Cell. la ld)) rd)))))))))
