(ns jaque.nock
  (:refer-clojure :exclude [inc dec atom])
  (:require [slingshot.slingshot :refer :all]
            [jaque.error :as e]
            [jaque.noun :refer :all]
            [jaque.cord :refer :all]
            [jaque.util :refer [bits cell-list]]
            [jaque.jets.math :refer [inc dec lth gth]]
            [jaque.jets.hash :refer [sham]]
            [jaque.jets.bit-surgery :refer [cut met]])
  (:import (jaque.noun Atom Cell)))

(defrecord State [data cold warm hot])
(def empty-state (map->State {:data []
                              :cold {}
                              :warm {}
                              :hot  {}}))

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

(defn fsck-clue [clu]
  (when-not (and (cell? clu) (cell? (tal clu)))
    (throw+ {:message "clue is not a triple"
             :clue     clu}))
  (let [nam (chum (hed clu))]
    (let [t (tal clu)
          q (loop [q (hed t)]
              (if (and (cell? q) (= a10 (hed q)))
                (recur (tal (tal q)))
                q))]
      (when-not (cell? q)
        (throw+ {:message "Atomic clue axis"
                 :axis    q
                 :clue    clu}))
      (let [axe (if (= q (Cell. a1 a0))
                  a0
                  (let [h (hed q)
                        t (tal q)]
                    (when-not (.isZero h)
                      (throw+ {:message  "Illegal operator in clue axis"
                               :clue     clu
                               :operator h}))
                    (when-not (.isCat h)
                      (throw+ {:message "Indirect clue axis"
                               :clue    clu
                               :axis    t}))
                    t))
            huk (loop [m {}
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
                        (recur (assoc m pir qir) tr)))))]
        [nam axe huk]))))

; FIXME: Cold state should really be all-nouns, and for that to happen we need
; to write at least ~(put by m) and ~(get by m).
; Which we have done (jaque.jets.maps). So now we just need to come in here
; and use those instead of maps.
(defn mine [^State sat ^Cell clu cor]
  (try+
    (let [[nam axe huk] (fsck-clue clu)
          bat     (hed cor)
          cold    (:cold sat)
          [mop cup lab]
          (if (.isZero axe)
            [(Cell. nam (Cell. a3 (Cell. no (tal cor))))
             (cell no cor) 
             nil]
            (let [rah (at cor axe)]
              (if (or (nil? rah) (not (cell? rah)))
                (throw+ {:message (format "fast: %s is bogus" (cord->string nam))
                         :axe     axe
                         :rah     rah})
                (let [tab (hed rah)
                      cax ((:warm sat) tab)]
                  (if (nil? cax)
                    (throw+ {:message (format "fast: in %s, parent %x not found at %d"
                                              (cord->string nam)
                                              (.hashCode tab)
                                              (.intValue ^Atom axe))})
                    (let [hos (hed (hed (tal cax)))
                          puc (hed (tal (tal cax)))]
                      [(Cell. nam (Cell. axe (Cell. yes hos)))
                       (if (and (= axe a3) (= (hed puc) yes))
                         (Cell. yes cor)
                         (Cell. no tab))
                       (hed (tal (tal (hed cax))))]))))))
          soh  (sham mop)
          cuz  [cup huk]
          hoe  (cold soh)
          sab  (if (nil? hoe)
                 {bat cuz}
                 (assoc [hoe 1] bat cuz))
          cold (assoc cold soh [mop sab])]
      (println (format "%d jets registered in cold jet state." (count cold)))
      (assoc sat :cold cold))
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
                              (run ~'a (dao ~(fasm b) ~'pay)))]
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

