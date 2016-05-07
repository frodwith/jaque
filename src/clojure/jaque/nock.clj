(ns jaque.nock
  (:refer-clojure :exclude [inc dec atom])
  (:require [slingshot.slingshot :refer :all]
            [jaque.error :as e]
            [jaque.noun :refer :all]
            [jaque.cord :refer :all]
            [jaque.util :refer [bits cell-list]]
            [jaque.jets.math :refer [inc dec lth gth]]
            [jaque.jets.hash :refer [sham]]
            [jaque.jets.map  :refer [get-by put-by]]
            [jaque.jets.bit-surgery :refer [cut met]])
  (:import (jaque.noun Atom Cell)))

; simple cores (gates jetted by a clojure fn)
(defn scor [nsp syms]
  (map (fn [sym] {:name (name sym)
                  :arms [{:axis 2
                          :fn   (ns-resolve (the-ns nsp) sym)}]})
       syms))

(def ray (:ray ((fn line [acc dev]
                  (let [jax (:jax acc)
                        kid (:kid acc)
                        dev (assoc dev :jax jax)
                        acc (reduce line {:ray (conj (:ray acc) dev)
                                          :kid []
                                          :jax (+ 1 jax)}
                                    (:kids dev))]
                    (assoc-in (assoc acc :kid (conj kid jax))
                              [:ray jax :kids]
                              (:kid acc))))
                {:jax 0
                 :kid []
                 :ray []}
                {:name "k151"
                 :kids [{:name "mood"
                         :kids [{:name "hoon"
                                 :kids (concat (scor 'jaque.jets.math
                                                     '(add sub inc dec))
                                               (scor 'jaque.jets.bit-surgery
                                                     '(bex lsh rsh)))}]}]})))

(defrecord State [data cold warm hot])
(def empty-state (map->State {:data []
                              :cold a0
                              :warm {}
                              :hot  ray}))


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

(defn run [sub [bat pay]]
  (let [cod `(fn [~'a ~'pay]
               (let [[~'pod ~'pay] ~bat]
                 [~'pod ~'pay]))
        fun (eval cod)]
    (fun sub pay)))

(defn kick [^Cell cor axel ^State pay]
  (let [bat (hed cor)
        cax ((:warm pay) bat)
        axe (atom axel)

        ; a bit later on, we'll move these
        ; to the negative cax branch
        fom (at cor axe)
        roc (dao fom pay)
        pod (run cor roc)]
j
    (if cax
      (let [yay (hed cax)
            jax (.intValue (hed yay))
            dev ((:hot pay) jax)
            hap (tal yay)
            ; Wow - this is quite a ways to get! At this point, we
            ; discovered a bug in put-by and get-by. They need proper
            ; TESTS, and this would be a good time to implement a better
            ; jet protocol, as well. If the tests pass and this is still
            ; blowing up with "can't cast direct atom to cell", either
            ; your tests aren't finding the bug or we've passed something
            ; incorrectly to put-by..
            off (.intValue (get-by hap axe))
            arm ((:kids dev) off)
            fun (:fn arm)]
        ; remove this after you fix existing jets to take a core
        (prn fun)
        pod)
      pod)))

; Convenience for throwing away runtime state (jet bindings, etc)
(defn phi [fom]
  #(let [[pod _] (run % (dao fom empty-state))]
     pod))

(defn nock [sub fom]
  ((phi fom) sub))

(defn sint [^Atom hint])

(defn chum [p]
  (if (cell? p)
    (let [h (hed p)
          t (tal p)]
      (if (.isCat t)
        (string->cord (format "%s%d" (cord->string h) (.intValue t)))
        (throw+ {:message "Bad chum"
                 :chum    p})))
    p))

; for "simple nock formulae" that "always reduce to an axis".
; Basically hints containing simple formula, the formula [1 0],
; and the formula [0 n] where n is a tree address. Mostly this
; is used in jet binding. What we're doing is just drilling down
; to the axis. If it's not in an acceptable form, we throw.
(defn axe-fom [fom]
  (let [a (loop [a fom]
            (if (and (cell? a) (= a10 (hed a)))
              (recur (tal (tal a)))
              a))]
    (if (cell? a)
      (let [[p q] (seq ^Cell a)]
        (if (and (= p a1) (= q a0))
          a0
          (if (.isZero p)
            (if (.isCat q)
              q
              (throw+ {:message "Indirect axis formula"
                       :formula fom
                       :atom    q}))
            (throw+ {:message  "Illegal op in axis formula"
                     :formula  fom
                     :operator p}))))
      (throw+ {:message "Atomic axis formula"
               :formula fom
               :atom    a}))))

(defn fsck-clue [clu]
  (when-not (and (cell? clu) (cell? (tal clu)))
    (throw+ {:message "clue is not a triple"
             :clue     clu}))
  (let [nam (chum (hed clu))
        t   (tal clu)
        axe (axe-fom (hed t))
        huk (loop [m a0
                   r (tal t)]
              (if (atom? r)
                m
                (let [ir (hed r)
                      tr (tal r)]
                  (when (atom? ir)
                    (throw+ {:message "Bad pair in clue map"
                             :clue    clu
                             :pair    ir}))
                  (let [pir (hed ir)
                        qir (tal ir)]
                    (when (cell? pir)
                      (throw+ {:message "Bad key in clue map"
                               :clue    clu
                               :key     pir}))
                    (recur (put-by m pir qir) tr)))))]
        [nam axe huk]))

; returns jax of (mop core) in hot or nil
(defn hot-mine [^Cell mop cor warm hot]
  (let [[bane axis loob each] (seq mop)
        sib (if (= loob yes)
              (let [bat (hed (at cor axis))
                    cax (warm bat)
                    par (.intValue (hed (hed cax)))]
                (map hot (:kids (hot par))))
              [(hot 0)])
        nam (cord->string (hed mop))
        win (first (filter #(= (:name %) nam) sib))]
    (if win
      (:jax win)
      nil)))

(defn warm-hump [jax huk hot]
  (loop [i a0
         m a0
         a (:arms (hot jax))]
    (let [arm (first a)]
      (if arm
        (let [m (cond (contains? arm :name)
                      (let [n (:name arm)
                            c (string->cord n)
                            f (get-by huk c)
                            x (axe-fom f)]
                        (put-by m x i))

                      (contains? arm :axis)
                      (put-by m (atom (:axis arm)) i)

                      :else
                      (throw+ {:message "warm-hump: malformed arm"
                               :arm     arm}))]
          (recur (inc i) m (next a)))
        m))))

(defn mine [^State sat ^Cell clu cor]
  (try+
    (let [[nam axe huk] (fsck-clue clu)
          bat     (hed cor)
          warm    (:warm sat)
          [mop cup lab]
          (if (.isZero axe)
            [(cell nam a3 no (tal cor))
             (cell no cor)
             a0]
            (let [rah (at cor axe)]
              (if (or (nil? rah) (not (cell? rah)))
                (throw+ {:message (format "fast: %s is bogus" (cord->string nam))
                         :axe     axe
                         :rah     rah})
                (let [tab (hed rah)
                      cax (warm tab)]
                  (if (nil? cax)
                    (throw+ {:message (format "fast: in %s, parent %x not found at %d"
                                              (cord->string nam)
                                              (.hashCode tab)
                                              (.intValue ^Atom axe))})
                    (let [hos (hed (hed (tal cax)))
                          puc (hed (tal (tal cax)))]
                      [(cell nam axe yes hos)
                       (if (and (= axe a3) (= (hed puc) yes))
                         (Cell. yes cor)
                         (Cell. no tab))
                       (hed (tal (tal (hed cax))))]))))))
          soh  (sham mop)
          cuz  (cell cup huk)
          cold (:cold sat)
          hoe  (get-by cold soh)
          sab  (put-by (if (.isZero hoe) a0 (tal (tal hoe)))
                       bat cuz)
          cold (put-by cold soh (cell mop sab))
          hot  (:hot sat)
          pty  (cord->string nam)
          jax  (hot-mine mop cor warm hot)]
      (if (nil? jax)
        (do (println "No jet:" pty)
            (assoc sat :cold cold))
        (let [bal  (cell nam lab)
              cax  (cell (cell (atom jax)
                               (warm-hump jax huk hot) bal a0)
                         (cell soh mop)
                         cuz)
              warm (assoc warm bat cax)]
          (println "Jet:" pty)
          (assoc sat :cold cold :warm warm))))
    (catch Object e
      (prn e)
      sat)))

(defn dint [sat typ clu cor]
  (if (= typ "fast")
    (mine sat clu cor)
    sat))

; A nock compiler
; original idea from https://gist.github.com/burtonsamograd/29103c2dfaa67f4fd344
(defn dao [^Cell f ^State pay]
  (let [p (hed f)
        q (tal f)]
    (if (cell? p)
      (let [^Cell p p
            [m pay] (dao p pay)
            [n pay] (dao q pay)
            bat     `(let [[~'m ~'pay] ~m
                           [~'n ~'pay] ~n]
                       [(Cell. ~'m ~'n) ~'pay])]
        [bat pay])
      (let [op (.intValue ^Atom p)]
        (case op
          0  (let [bat `[~(fasm q) ~'pay]]
               [bat pay])

          1  (let [dat (:data pay)
                   i   (count dat)
                   pay (assoc pay :data (conj dat q))
                   bat `[((:data ~'pay) ~i) ~'pay]]
               [bat pay])

          2  (let [[b pay] (dao (hed q) pay)
                   [c pay] (dao (tal q) pay)
                   bat     `(let [[~'b ~'pay] ~b
                                  [~'c ~'pay] ~c]
                              (run ~'b (dao ~'c ~'pay)))]
               [bat pay])

          3  (let [[b pay] (dao q pay)
                   bat     `(let [[~'b ~'pay] ~b]
                              [(if (cell? ~'b) yes no) ~'pay])]
               [bat pay])

          4  (let [[b pay] (dao q pay)
                   bat     `(let [[~'b ~'pay] ~b]
                              [(inc ~'b) ~'pay])]
               [bat pay])

          5  (let [[b pay] (dao (hed q) pay)
                   [c pay] (dao (tal q) pay)
                   bat     `(let [[~'b ~'pay] ~b
                                  [~'c ~'pay] ~c]
                              [(if (= ~'b ~'c) yes no) ~'pay])]
               [bat pay])

          6  (let [[b pay] (dao (hed q) pay)
                   qq      (tal q)
                   [c pay] (dao (hed qq) pay)
                   [d pay] (dao (tal qq) pay)
                   bat     `(let [[^Atom ~'b ~'pay] ~b]
                              (if (.isZero ~'b) ~c ~d))]
               [bat pay])

          7  (let [[b pay] (dao (hed q) pay)
                   [c pay] (dao (tal q) pay)
                   bat     `(let [[~'a ~'pay] ~b] ~c)]
               [bat pay])

          8  (let [[b pay] (dao (hed q) pay)
                   [c pay] (dao (tal q) pay)
                   bat     `(let [[~'b ~'pay] ~b
                                  ~'a (Cell. ~'b ~'a)]
                              ~c)]
               [bat pay])

          9  (let [b       (hed q)
                   [c pay] (dao (tal q) pay)
                   bat     `(let [[~'a ~'pay] ~c]
                              (kick ~'a ~(.intValue b) ~'pay))]
               [bat pay])

          10 (let [b         (hed q)
                   c         (tal q)
                   [bat pay] (dao c pay)]
               (if (atom? b)
                 (do (sint b)
                     [bat pay])
                 (let [typ       (cord->string (hed b))
                       fom       (tal b)
                       [hif pay] (dao fom pay)
                       bat       `(let [[~'c ~'pay] ~bat
                                        [~'h ~'pay] ~hif]
                                    [~'c (dint ~'pay ~typ ~'h ~'c)])]
                   [bat pay]))))))))

