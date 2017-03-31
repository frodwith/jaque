(ns ack
  (:require [jaque.noun :refer [noun]])
  (:import net.frodwith.jaque.truffle.Context
           net.frodwith.jaque.data.Cell
           (net.frodwith.jaque.truffle.driver Arm AxisArm)
           net.frodwith.jaque.truffle.nodes.jet.DecNodeGen))

(def context (Context. (into-array Arm [(AxisArm. "main/dec" 2 DecNodeGen)])))

(defn nock [bus fol]
  (.nock context bus fol))

(def kernel-formula
  (noun [7 [1 :ackermann] 8 [1 [7 [8 [1 0] [1 6 [5 [1 0] 0 6] [0 0] 8 [1 0] 8 [1 6 [5 [4 0 6] 0 30] [0 6] 9 2 [0 2] [4 0 6] 0 7] 9 2 0 1] 0 1] 10 [:fast 1 :dec [0 7] 0] 0 1] 7 [8 [1 0 0] [1 6 [5 [1 0] 0 12] [4 0 13] 6 [5 [1 0] 0 13] [9 2 [0 2] [[8 [9 4 0 7] 9 2 [0 4] [0 28] 0 11] 1 1] 0 7] 9 2 [0 2] [[8 [9 4 0 7] 9 2 [0 4] [0 28] 0 11] 9 2 [0 2] [[0 12] 8 [9 4 0 7] 9 2 [0 4] [0 29] 0 11] 0 7] 0 7] 0 1] 10 [:fast 1 :ack [0 7] 0] 0 1] 10 [:fast 1 :main [1 0] 0] 0 1]))

(def args (noun [3 9]))
(def call-formula (noun [7 [9 5 0 1] 9 2 [0 2] [1 args] 0 7]))

; we could save some more fragments if we could inline the battery and context
; frag with some Location knowledge.... actually that might even eliminate
; fine checks? it's possible in theory...

; possibly also subclasses for TailKickNode and TailNockNode. abstract .call,
; throw for tails and dispatch for others.


; < 1 sec!
(defn ack [m n]
  (if (zero? m)
    (inc n)
  (if (zero? n)
    (ack (dec m) 1)
  (ack (dec m) (ack m (dec n))))))

(defn -main []
  (let [r1 (nock 0 kernel-formula)
        start (java.util.Date.)
        yawn  (prn start)
        ;r2    (ack 3 9)]
        r2 (nock r1 call-formula)]
    (prn r2)
    (prn (java.util.Date.))))
