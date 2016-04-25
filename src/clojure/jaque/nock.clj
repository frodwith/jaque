(ns jaque.nock
  (:refer-clojure :exclude [inc dec atom])
  (:require [slingshot.slingshot :refer :all]
            [jaque.error :as e]
            [jaque.noun :refer :all]
            [jaque.cord :refer :all]
            [jaque.jets.math :refer [inc dec lth]]
            [jaque.jets.bit-surgery :refer [cut rsh]])
  (:import (jaque.noun Atom Cell)))

(defn fas [^Atom axe]
  (loop [dir nil
         axe axe]
    (if (lth axe a2)
      (reduce (fn [f d] (if d `(hed ~f) `(tal ~f)))
              'a dir)
      (recur (cons (.isZero (cut a0 a0 a1 axe)) dir)
             (rsh a0 a1 axe)))))

(defn axis [^Cell sub ^Atom axe]
  (let [fun (eval `(fn [~'a] ~(fas axe)))]
    (try
      (fun sub)
      (catch ClassCastException e
        (e/exit)))))

(declare dao)

(defn phi [fom]
  (let [[bat pay] (dao fom [])
        code      `(fn [~'q ~'a] ~bat)
        _         (println (str fom))
        fun       (eval code)]
    (println "...done.")
    (partial fun pay)))

(defn nock [sub fom]
  ((phi fom) sub))

; A nock compiler
; original idea from https://gist.github.com/burtonsamograd/29103c2dfaa67f4fd344

; We started out generating code for quoted atoms, but that blew the java
; heap. So, we return a core from dao - i.e. some generated code and some data
; it will be allowed to reference when it's executed. The generated code
; references this data to implement quoting.

(defn dao [^Cell f pay]
  (let [p (hed f)
        q (tal f)]
    (if (cell? p)
      (let [^Cell p p
            [m pay] (dao p pay)
            [n pay] (dao q pay)
            bat     `(cell ~m ~n)]
        [bat pay])
      (let [op (.intValue ^Atom p)]
        (println op)
        (case op
          0  [(fas q) pay]

          1  (let [bat `(~'q ~(count pay))
                   pay (conj pay q)]
               [bat pay])

          2  (let [[b pay] (dao (hed q) pay)
                   [c pay] (dao (tal q) pay)
                   bat     `(nock ~b ~c)]
               [bat pay])

          3  (let [[b pay] (dao q pay)
                   bat     `(if (cell? ~b) yes no)]
               [bat pay])

          4  (let [[b pay] (dao q pay)
                   bat     `(inc ~b)]
               [bat pay])

          5  (let [[b pay] (dao (hed q) pay)
                   [c pay] (dao (tal q) pay)
                   bat     `(if (= ~b ~c) yes no)]
               [bat pay])

          6  (let [[b pay] (dao (hed q) pay)
                   qq      (tal q)
                   [c pay] (dao (hed qq) pay)
                   [d pay] (dao (tal qq) pay)
                   bat     `(if (.isZero ^Atom ~b) ~c ~d)]
               [bat pay])

          7  (let [[b pay] (dao (hed q) pay)
                   [c pay] (dao (tal q) pay)
                   bat     `(let [~'a ~b] ~c)]
               [bat pay])

          8  (let [[b pay] (dao (hed q) pay)
                   [c pay] (dao (tal q) pay)
                   bat     `(let [~'a (cell ~b ~'a)] ~c)]
               [bat pay])

          9  (let [b       (hed q)
                   [c pay] (dao (tal q) pay)
                   bat     `(let [~'d ~c
                                  ~'e (let [~'a ~'d] ~(fas b))]
                              (nock ~'d ~'e))]
               [bat pay])

          10 (let [b         (hed q)
                   c         (tal q)
                   [bat pay] (dao c pay)]
               (if (atom? b)
                 (do (println (format "static hint: %s" b))
                     [bat pay])
                 (let [nam (hed b)
                       fom (tal b)
                       bat `(let [~'hin ~(dao fom pay)]
                              (print "Computed dynamic hint:")
                              (prn ~'hin))]
                   (println (format "compiled %%%s" (cord->string nam)))
                   [bat pay]))))))))
