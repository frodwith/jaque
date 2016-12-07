(ns jaque.jets.v2
  (:refer-clojure :exclude [zero?])
  (:require [jaque.noun.read :refer [if& zero? lark head]]
            [jaque.jets.util :refer [defjet]]
            [jaque.math :as m])
  (:import (jaque.noun Atom)))

(defjet bex [+<] [a]
  (m/bex a))

(defjet by-get [+>+< +<] [a b]
  (loop [a a]
  (if (zero? a)
    a0
  (if (= b (lark -< a))
    (cell a0 (lark -> a))
  (recur (if& (gor b (lark -< a))
           (lark +< a)
         (lark +> a)))))))

(defjet by-put [+>+< +<- +<+] [a b c]
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

(defjet cap [+<] [a]
  (m/cap a))

(defjet con [+<- +<+] [^Atom a ^Atom b]
  (.con a b))

(defjet lsh [+< +>- +>+] [a b c]
  (m/lsh a b c))

(defjet mas [+<] [a]
  (m/mas a))

(defjet sub [+<- +<+] [^Atom a ^Atom b]
  (.sub a b))
