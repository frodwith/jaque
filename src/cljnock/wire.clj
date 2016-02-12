(ns cljnock.wire
  (:refer-clojure :exclude [cat])
  (:require [cljnock.core :refer :all])
  (:require [cljnock.bit :refer :all]))

(defn mat [a]
  (if (= a 0)
    [1 1]
    (let [b (met 0 a)
          c (met 0 b)
          l (+ c c b)
          d (cat 0 (bex c) (mix (end 0 (dec c) b) (lsh 0 (dec c) a)))]
      [l d])))

(defn rub [a b]
  (let [m (met 0 b)
        c (loop [c 0]
            (assert (<= c m))
            (if (= 0 (cut 0 [(+ a c) 1] b))
              (recur (inc c))
              c))]
    (if (= 0 c)
      [1 0]
      (let [d  (+ a c 1)
            dc (dec c)
            e  (+ (bex dc) (cut 0 [d dc] b))]
        [(+ c c e) (cut 0 [(+ d dc) e] b)]))))

(defn- jam-atom [a m]
  (let [[p q] (mat a)]
    [(inc p) (lsh 0 1 q) m]))

(defn jam [a]
  (((fn $ [a b m]
     (if-not (contains? m a)
       (let [m (assoc m a b)]
         (if (atom? a)
           (jam-atom a m)
           (let [b (+ 2 b)
                 [pd qd rd] ($ (a 0) b m)
                 [pe qe re] ($ (a 1) (+ b pd) rd)]
             [(+ 2 pd pe) (mix 1 (lsh 0 2 (cat 0 qd qe))) re])))
       (let [c (m a)]
         (if (and (atom? a) (<= (met 0 a) (met 0 c)))
           (jam-atom a m)
           (let [[p q] (mat c)]
             [(+ 2 p) (mix 3 (lsh 0 2 q)) m])))))
   a 0 {}) 1))

(defn cue [a]
  (((fn $ [b m]
      (if (= 0 (cut 0 [b 1] a))
        (let [[pc qc] (rub (inc b) a)]
          [(inc pc) qc (assoc m b qc)])
        (let [c (+ 2 b)]
          (if (= 0 (cut 0 [(inc b) 1] a))
            (let [[pu qu ru] ($ c m)
                  [pv qv rv] ($ (+ pu c) ru)
                  w          [qu qv]]
              [(+ 2 pu pv) w (assoc rv b w)])
            (let [[pd qd] (rub c a)]
              [(+ 2 pd) (m qd) m])))))
   0 {}) 1))
