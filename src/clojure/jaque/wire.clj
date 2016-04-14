(ns jaque.wire
  (:require [jaque.atom :refer [lsh rsh mix
                                cut end
                                lot met bex]
                        :as    a]
            [jaque.noun :refer [->Cell atom?]])
  (:import [jaque.noun Atom Cell]))

(set! *warn-on-reflection* true)

(defn rub "length-decode"
  [a ^Atom b]
  (let [c (lot a b)]
    (if (= 0 c)
      [1 a/zero]
      (let [d  (+ 1 a c)
            dc (dec c)
            e  (a/add (bex dc) (cut 0 d dc b))
            ei (.longValue e)]
        [(+ ei c c) (cut 0 (+ d dc) ei b)]))))

(defn mat "length-encode"
  [^Atom a]
  (if (a/zero? a)
    [1 a/one]
    (let [b  (met 0 a)
          ba (Atom/fromLong b)
          c  (met 0 ba)
          s  (+ b c c)
          l  (bex c)
          r  (mix (end 0 (dec c) ba)
                  (lsh 0 (dec c) a))
          e (a/cat 0 l r)]
      [s e])))

(defn cue "unpack atom to noun"
  [^Atom a]
  (let [go (fn $ [b m]
             (if (a/zero? (cut 0 b 1 a))
               (let [[pc qc] (rub (inc b) a)]
                 [(inc pc) qc (assoc! m b qc)])
               (let [c (+ 2 b)]
                 (if (a/zero? (cut 0 (inc b) 1 a))
                   (let [[pu qu ru] ($ c m)
                         [pv qv rv] ($ (+ pu c) ru)
                         w          (->Cell qu qv)]
                     [(+ 2 pu pv) w (assoc! rv b w)])
                   (let [[pd qd] (rub c a)
                         got     (m (.longValue ^Atom qd))]
                     (if (nil? got)
                       (throw (Exception. (format "No cached noun at index %s" qd)))
                       [(+ 2 pd) got m]))))))
        [p q r] (go 0 (transient {}))]
    q))

(defn jam "pack"
  [a]
  (let [go (fn $ [a b m]
             (let [c (m a)]
               (if (nil? c)
                 (let [m (assoc! m a b)]
                   (if (atom? a)
                     (let [[pd qd] (mat a)]
                       [(inc pd) (lsh 0 1 qd) m])
                     (let [b          (+ 2 b)
                           [pd qd rd] ($ (.p ^Cell a) b m)
                           [pe qe re] ($ (.q ^Cell a) (+ b pd) rd)]
                       [(+ 2 pd pe) (mix a/one (lsh 0 2 (a/cat 0 qd qe))) re])))
                 (let [c (Atom/fromLong c)]
                   (if (and (atom? a) (<= (met 0 a) (met 0 c)))
                     (let [[pd qd] (mat a)]
                       [(inc pd) (lsh 0 1 qd) m])
                     (let [[pd qd] (mat (Atom/fromLong c))]
                       [(+ 2 pd) (mix a/three (lsh 0 2 qd)) m]))))))
        [p q r] (go a 0 (transient {}))]
    q))
