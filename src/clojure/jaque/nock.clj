(ns jaque.nock
  (:refer-clojure :exclude [inc dec atom])
  (:require [slingshot.slingshot :refer :all]
            [jaque.error :as e]
            [jaque.noun :refer :all]
            [jaque.cord :refer :all]
            [jaque.util :refer [bits]]
            [jaque.jets.math :refer [inc dec lth gth]]
            [jaque.jets.bit-surgery :refer [cut met]])
  (:import (jaque.noun Atom Cell)))

(defn pre [top fun]
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

(defn sint [^Atom hint])

; Honestly, i should probably redo this. But i'm not ready to yet.
; I might need to spend more time understanding the c3j_mine code
; path.

; It looks like they use bash (which is a sha256 of the battery,
; basically) as the key for 
(defn fast [^Cell clu cor]
  )
;  (when (cell? cor)
;    (let [batt (hed cor)
;          calx (finj batt)]
;      (when (and calx (fsck-clue clu))
;        (let [p       (hed clu)
;              t       (tal clu)
;              ^Atom q (hed t)
;              r       (tal t)
;              chum    (if (cell? p)
;                        (str (cord->string (hed p)) (tal p))
;                        (cord->string hid))
;
;              [^Atom cope ^Atom corp ^Atom path] 
;              (if (.isZero q)
;                [(cell p a3 no cor) (cell no cor) a0]
;                (let [rah (axis q cor)]
;                  (if (or (nil? rah) (not (cell? rah)))
;                    (println (format "fund: %s is bogus" q))
;                    (let [tab (hed rah)
;                          cax (finj tab)]
;                      (if (nil? cax)
;                        (println 
;                          (format "fund: in %s, parent %x not found at %d"
;                                  p (.hashCode tab) q))
;                        ; Maybe we finally do something here...
;                        )))))
;
;              bash    (.hashCode cope) ; this might be wrong - investigate cj_sham
;              club    (Cell. corp r)]
;          ; do something neat
;          )))))

(defn dint [typ ^Cell clu cor]
  (if (= typ "fast")
    (fast clu cor)
    nil))

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
          0  [(fasm q) pay]

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
                              (run ~'a (dao ~(fasm b) ~'q)))]
               [bat pay])

          10 (let [b         (hed q)
                   c         (tal q)
                   [bat pay] (dao c pay)]
               (if (atom? b)
                 (do (sint b)
                     [bat pay])
                 (let [nam       (cord->string (hed b))
                       fom       (tal b)
                       [hif pay] (dao fom pay)
                       bat       `(let [~'c ~bat] (dint ~nam ~hif ~'c) ~'c)]
                   [bat pay]))))))))

