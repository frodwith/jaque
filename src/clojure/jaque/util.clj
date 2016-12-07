(ns jaque.util
  (:refer-clojure :exclude [inc met atom])
  (:require [jaque.jets.math :refer [inc lth]]
            [jaque.jets.bit-surgery :refer [met cut]]
            [jaque.noun :refer :all])
  (:import (jaque.noun Atom Noun)))

(defn- pre [top fun]
  (let [ans (reduce (fn [m n] (assoc m n (fun n))) {} (map atom (range top)))
        top (atom top)]
    (fn [^Atom i]
      (if (lth i top)
        (ans i)
        (fun i)))))

; generates code that throws ClassCastException
; if axe isn't a valid path to a noun at runtime
(defn fas [^Atom axe]
  (reduce (fn [f ^Atom bit] `(. ~(with-meta f {:tag 'jaque.noun.Cell})
                                ~(if (.isZero bit) 'p 'q)))
          'a (rest (reverse (bits axe)))))

; We pre-compute fas to an arbitrary upper limit rather than memoizing,
; because that's a quick way to run out of memory when the domain
; of your function is the natural numbers. Anyway, the vast majority
; of fas calls are going to be within this range.
(def fasm (pre 256 fas))
(def fasf (pre 256 #(eval `(fn [~'a] ~(fasm %)))))

(defn at [sub axe]
  (try ((fasf axe) sub)
    (catch ClassCastException _
      nil)))

(defn bits [^Atom a]
  (let [len (met a0 a)
        f   (fn bits-in [i]
              (when (lth i len)
                (lazy-seq (cons (cut a0 i a1 a)
                                (bits-in (inc i))))))]
    (f a0)))

(defn cell-list [c]
  (if (cell? c)
    (lazy-seq (cons (hed c) (cell-list (tal c))))
    c))

(defn lark [s]
  (if-not (re-find #"^[+-](?:[<>][+-])*[<>]?$" s)
    0
    (let [bits (map #(case % \- \0
                             \+ \1
                             \< \0
                             \> \1)
                    (seq s))]
      (Atom/fromString (apply str (conj bits \1)) 2))))

(defn mean [x as]
  (reverse (reduce #(cons (at x %2) %1) nil as)))

(defn mean-wrap [f m] #(apply f (mean % m)))
