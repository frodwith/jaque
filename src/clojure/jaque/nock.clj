(ns jaque.nock
  (:refer-clojure :exclude [inc dec atom])
  (:require [slingshot.slingshot :refer :all]
            [jaque.error :as e]
            [jaque.noun :refer :all]
            [jaque.cord :refer :all]
            [jaque.jets.math :refer [inc dec lth]]
            [jaque.jets.bit-surgery :refer [cut rsh]])
  (:import (jaque.noun Atom Cell)))

(defn axis [^Cell sub ^Atom axe]
  (loop [dir nil
         axe axe]
    (if (lth axe a2)
      (try
        (reduce (fn [^Cell c dir] (if (= 0 dir) (hed c) (tal c)))
                sub dir)
        (catch ClassCastException ex (e/exit)))
      (recur (cons (if (.isZero (cut a0 a0 a1 axe)) 0 1) dir)
             (rsh a0 a1 axe)))))

(defn daofas [^Atom b]
  (cond (= b a1) 'a
        (= b a2) `(hed ~'a)
        (= b a3) `(tal ~'a)
        :else (let [even (.isZero (cut a0 a0 a1 b))
                    newb (rsh a0 a1 (if even b (dec b)))
                    nexf (daofas newb)]
                (if even
                  `(hed ~nexf)
                  `(tal ~nexf)))))

(defn daoqot [f]
  (cond (cell? f)
          (let [m (daoqot (hed f))
                n (daoqot (tal f))]
            `(cell ~m ~n))
        (atom? f)
          (if (.isCat ^Atom f)
            `(Atom/fromLong ~(.longValue ^Atom f))
            `(Atom/malt (int-array ~(into [] (.words ^Atom f)))))
        (= f 'a)
          f
        :else
          (throw+ {:type ::bad-quote :form f})))

(declare dao)

(def phi
  (memoize
    (fn [f]
      (let [body (dao f)
            code `(fn [~'a] ~body)]
        (eval code)))))

; A nock compiler
; adapted from https://gist.github.com/burtonsamograd/29103c2dfaa67f4fd344
(defn dao [f]
  (if-not (cell? f)
    f
    (let [^Cell f f
          p (hed f)
          q (tal f)]
      (if (cell? p)
        (let [^Cell p p
              m (dao p)
              n (dao q)]
          `(cell ~m ~n))
        (case (.intValue ^Atom p)
          0  (daofas q)
          1  (daoqot q)
          2  (let [bc      (dao (hed q))
                   ^Cell d (dao (tal q))]
               (if (= (first d) 'quote)
                 (let [x (dao (hed (tal d)))]
                   (if (or (= bc 'a)
                           (atom? x))
                     (daoqot x)
                     `(let [a ~bc]
                        ~x)))
                 `((phi ~d) ~bc)))
          3  `(if (cell? ~(dao q)) yes no)
          4  `(inc ~(dao q))
          5  (let [m (hed q)
                   n (tal q)]
               `(if (= ~(dao m) ~(dao n)) yes no))
          6  (let [^Atom b (dao (hed q))
                   qq      (tal q)
                   c       (dao (hed qq))
                   d       (dao (tal qq))]
               `(if (.isZero ~b) ~c ~d))
          7  (let [b (dao (hed q))
                   c (dao (tal q))]
               `((fn [~'a] ~c) ((fn [~'a] ~b) ~'a)))
          8  (let [b (dao (hed q))
                   c (dao (tal q))]
               `(let [~'a (cell ~b ~'a)]
                  ~c))
          9  (let [b (dao (hed q))
                   c (dao (tal q))]
               `(let [~'f (fn [~'a] ~c)
                      ~'x (~'f ~'a)]
                  ((phi (let [~'a ~'x] ~(daofas b))) ~'x)))
          10 (let [b (hed q)
                   c (tal q)
                   r (dao c)]
               (if (atom? b)
                 r
                 (let [nam (hed b)
                       fom (tal b)]
                   (println (format "compiling dynamic hint: %s" (cord->string nam)))
                   `(let [hin ~(dao fom)]
                      (println (format "dynamic hint: %s" hin))
                      ~r)))))))))
