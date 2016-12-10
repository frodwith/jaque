(ns jaque.noun.read
  (:refer-clojure :exclude [zero?])
  (:require [jaque.noun.print]
            [jaque.error :refer [bail]]
            [jaque.constants :refer [yes no a0 a1 a2]]
            [jaque.math :refer [cap mas]])
  (:import (jaque.noun Atom Cell Noun)))

(def atom? (partial instance? Atom))
(def cell? (partial instance? Cell))
(def noun? (partial instance? Noun))

(defn zero? [^Noun n] (.isZero n))
(defn head [^Cell c] (.p c))
(defn tail [^Cell c] (.q c))

(defn cord->string ^String [^Atom c]
  (String. (.toByteArray c Atom/LITTLE_ENDIAN) "UTF-8"))

(defn fragment [^Atom axis ^Noun subject]
  (loop [a axis
         n subject]
  (if (= a1 a)
    n
  (if-not (cell? n)
    nil
  (recur (mas a)
         ((if (= a2 (cap a)) head tail) n))))))

(defn mean [x & as]
  (map #(fragment % x) as))

(defn ^Atom lark->axis [s]
  (if-not (re-find #"^[+-](?:[<>][+-])*[<>]?$" s)
    a0
    (let [bits (map #(case % \- \0
                             \+ \1
                             \< \0
                             \> \1)
                    (seq s))]
      (Atom/fromString (apply str (conj bits \1)) 2))))

(defmacro lark [sym n]
  `(fragment ~(lark->axis (name sym)) ~n))

(defn trel-seq [^Noun n]
  (or
    (and (cell? n)
    (let [p (head n)
          x (tail n)]
    (and (cell? x)
    (let [q (head x)
          r (tail x)]
    [p q r]))))
  nil))

(defn nlr-seq [^Noun n]
  (let [[n l r] (trel-seq n)]
  (if (or (nil? n) (zero? n))
    nil
  (concat (nlr-seq l) (cons n (nlr-seq r))))))

(defmacro if& [t y n]
  `(let [r# ~t]
     (cond (= yes r#) ~y
           (= no  r#) ~n
           :else      (bail :exit))))

(defmacro if| [t y n]
  `(let [r# ~t]
     (cond (= no  r#) ~y
           (= yes r#) ~n
           :else      (bail :exit))))
