(ns jaque.jets.v2
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.noun.read :refer [if& atom? zero? lark head tail]]
            [jaque.noun.box :refer :all]
            [jaque.constants :refer :all]
            [jaque.jets.jet :refer [defjet]]
            [jaque.math :as m])
  (:import (jaque.noun Atom Noun)))

(defjet bex [k151 mood hoon bex] - [+<] [a]
  (m/bex a))

(defjet mug [k151 mood hoon mug] - [+<] [^Noun a]
  (atom (.hashCode a)))

(defjet lth [k151 mood hoon lth] - [+<- +<+] [^Atom a ^Atom b]
  (if (= -1 (.compareTo a b)) yes no))

(defjet dor [k151 mood hoon dor] - [+<- +<+] [a b]
  (loop [a a
         b b]
  (if (= a b)
    yes
  (if (atom? a)
    (if (atom? b)
      (lth a b)
    yes)
  (if (atom? b)
    no
  (if (= (head a) (head b))
    (recur (tail a) (tail b))
  (recur (head a) (head b))))))))

(defjet gor [k151 mood hoon gor] - [+<- +<+] [a b]
  (let [c (mug a)
        d (mug b)]
    (if (= c d)
      (dor a b)
      (lth c d))))

(defjet vor [k151 mood hoon vor] - [+<- +<+] [a b]
  (let [c (mug (mug a))
        d (mug (mug b))]
    (if (= c d)
      (dor a b)
      (lth c d))))

(defjet by-get [k151 mood hoon by get] - [+>+< +<] [a b]
  (loop [a a]
  (if (zero? a)
    a0
  (if (= b (lark -< a))
    (cell a0 (lark -> a))
  (recur (if& (gor b (lark -< a))
           (lark +< a)
         (lark +> a)))))))

(defjet by-put [k151 mood hoon by put] - [+>+< +<- +<+] [a b c]
  (if (zero? a)
    (cell (cell b c) a0 a0)
  (if (= b (lark -< a))
    (if (= c (lark -> a))
      a
    (cell (cell b c) (lark +< a) (lark +> a)))
  (if& (gor b (lark -< a))
    (let [d (by-put (lark +< a) b c)]
    (if& (vor (lark -< a) (lark -< d))
      (cell (head a) d (lark +> a))
    (cell (head d) 
          (lark +< d)
          (head a)
          (lark +> d)
          (lark +> a))))
  (let [d (by-put (lark +> a) b c)]
  (if& (vor (lark -< a) (lark -< d))
    (cell (head a) (lark +< a) d)
  (cell (head d)
        (cell (head a)
              (lark +< a)
              (lark +< d))
        (lark +> d))))))))

(defjet cap [k151 mood hoon cap] - [+<] [a]
  (m/cap a))

(defjet con [k151 mood hoon con] - [+<- +<+] [^Atom a ^Atom b]
  (Atom/con a b))

(defjet lsh [k151 mood hoon lsh] - [+< +>- +>+] [a b c]
  (m/lsh a b c))

(defjet mas [k151 mood hoon mas] - [+<] [a]
  (m/mas a))

(defjet sub [k151 mood hoon sub] - [+<- +<+] [^Atom a ^Atom b]
  (.sub a b))
