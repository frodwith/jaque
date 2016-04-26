(ns jaque.nock
  (:refer-clojure :exclude [inc dec atom])
  (:require [slingshot.slingshot :refer :all]
            [jaque.error :as e]
            [jaque.noun :refer :all]
            [jaque.cord :refer :all]
            [jaque.jets.math :refer [inc dec gth]]
            [jaque.jets.bit-surgery :refer [cut met]])
  (:import (jaque.noun Atom Cell)))

(defn fas [^Atom axe]
  (if (.isZero axe)
    'a
    (let [end (dec (met a0 axe))]
      (loop [dir nil
             i   a0]
        (if (= i end)
          (reduce (fn [f d] (if d `(hed ~f) `(tal ~f)))
                            'a dir)
          (recur (cons (.isZero (cut a0 i a1 axe)) dir)
                 (inc i)))))))

(defn axis [^Cell sub ^Atom axe]
  (let [fun (eval `(fn [~'a] ~(fas axe)))]
    (try
      (fun sub)
      (catch ClassCastException e
        (e/exit)))))

(declare dao)

(defn run [sub cor]
  (let [[bat pay] cor
        cod `(fn [~'q ~'a] ~bat)
        fun (eval cod)]
    (fun pay sub)))

(defn phi [fom]
  #(run % (dao fom [])))

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
            bat     `(Cell. ~m ~n)]
        [bat pay])
      (let [op (.intValue ^Atom p)]
        (case op
          0  [(fas q) pay]

          1  (let [i   (count pay)
                   bat `(~'q ~i)
                   pay (conj pay q)]
               [bat pay])

          2  (let [[b pay] (dao (hed q) pay)
                   [c pay] (dao (tal q) pay)
                   bat     `(run ~b (dao ~c ~'q))]
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
                   bat     `(let [~'a (Cell. ~b ~'a)] ~c)]
               [bat pay])

          9  (let [b       (hed q)
                   [c pay] (dao (tal q) pay)
                   bat     `(let [~'a ~c]
                              (run ~'a (dao ~(fas b) ~'q)))]
               [bat pay])

          10 (let [b         (hed q)
                   c         (tal q)
                   [bat pay] (dao c pay)]
               (if (atom? b)
                 (do (println (format "static hint: %s" b))
                     [bat pay])
                 (let [nam       (cord->string (hed b))
                       fom       (tal b)
                       [hif pay] (dao fom pay)
                       bat       `(let [~'hin ~hif]
                                    (println "Computed dynamic hint")
                                    ~(if (= nam "fast")
                                       `(let [~'hid (hed ~'hin)
                                              ~'nam (if (cell? ~'hid)
                                                      (format "%s%s" (cord->string (hed ~'hid)) (tal ~'hid))
                                                      (cord->string ~'hid))]
                                          (println (format "%%fast hint: %%%s" ~'nam))
                                          ~bat)
                                       bat))]
                   (println (format "compiled %%%s" nam))
                   [bat pay]))))))))
