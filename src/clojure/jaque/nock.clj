(ns jaque.nock
  (:refer-clojure :exclude [inc dec atom])
  (:require [slingshot.slingshot :refer :all]
            [jaque.error :as e]
            [jaque.noun :refer :all]
            [jaque.jets.math :refer [inc dec lth]]
            [jaque.jets.bit-surgery :refer [cut rsh]])
  (:import (jaque.noun Atom Cell)))

(defn axis [^Cell sub ^Atom axe]
  (loop [dir nil
         axe axe]
    (if (lth axe a2)
      (try
        (reduce (fn [^Cell c dir] (if (= 0 dir) (.p c) (.q c)))
                sub dir)
        (catch ClassCastException ex (e/exit)))
      (recur (cons (if (.isZero (cut a0 a0 a1 axe)) 0 1) dir)
             (rsh a0 a1 axe)))))

(defn daofas [^Atom b]
  (cond (= b a1) 'a
        (= b a2) '(.p a)
        (= b a3) '(.q a)
        :else (let [even (.isZero (cut a0 a0 a1 b))
                    newb (rsh a0 a1 (if even b (dec b)))
                    nexf (daofas newb)]
                (if even
                  `(.p ~nexf)
                  `(.q ~nexf)))))

(defn daoqot [f]
  (cond (cell? f)
          `(cell ~(daoqot (.p f)) ~(daoqot (.q f)))
        (atom? f)
          `(atom ~(read-string (str f)))
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
    (let [p (.p f)
          q (.q f)]
      (if (cell? p)
        (let [m (dao p)
              n (dao q)]
          `(cell ~m ~n))
        (case (.intValue p)
          0  (daofas q)
          1  (daoqot q)
          2  (let [bc (dao (.p q))
                   d  (dao (.q q))]
               (if (= (first d) 'quote)
                 (let [x (dao (.p (.q d)))]
                   (if (or (= bc 'a)
                           (atom? x))
                     (daoqot x)
                     `(let [a ~bc]
                        ~x)))
                 `((phi ~d) ~bc)))
          3  `(if (cell? ~(dao q)) yes no)
          4  `(inc ~(dao q))
          5  (let [m (.p q)
                   n (.q q)]
               `(if (= ~(dao m) ~(dao n)) yes no))
          6  (let [b  (dao (.p q))
                   qq (.q q)
                   c  (dao (.p qq))
                   d  (dao (.q qq))]
               `(if (.isZero ~b) ~c ~d))
          7  (let [b (dao (.p q))
                   c (dao (.q q))]
               `((fn [~'a] ~c) ((fn [~'a] ~b) ~'a)))
          8  (let [b (dao (.p q))
                   c (dao (.q q))]
               `(let [~'a (cell ~b ~'a)]
                  ~c))
          9  (let [b (dao (.p q))
                   c (dao (.q q))]
               `(let [~'f (fn [~'a] ~c)
                      ~'x (~'f ~'a)]
                  ((phi (let [~'a ~'x] ~(daofas b))) ~'x)))
          10 (let [b (.p q)
                   c (.q q)
                   r (dao c)]
               (if (atom? b)
                 r
                 `(do ~(dao (.q b))
                      ~r))))))))
