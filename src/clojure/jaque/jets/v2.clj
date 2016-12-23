(ns jaque.jets.v2
  (:refer-clojure :exclude [inc dec cat])
  (:require [jaque.jets.jet :refer [defjet]]
            [jaque.noun.math :as math]
            [jaque.noun.hash :as hash]
            [jaque.noun.bits :as bits]
            [jaque.noun.pack :as pack]
            [jaque.noun.nlr  :as nlr]))
            
;; All jets in volume 2 of hoon.hoon should be defined here. The macros below
;; are provided as a convenience for jetting a gate at axis 2 of a core
;; labeled starting with its name and with the hoon core as the parent (e.g.
;; cap goes to [cap hoon mood k151]) with arbitrary, one, cell, or trel
;; arguments respectively.

(defmacro defgate [n lark f]
  `(defjet ~n [~n hoon mood k151] - ~lark (ignore-machine f)))

(defmacro defg1 [n & body]
  `(defgate ~n [+<] ~@body))

(defmacro defg2 [n & body]
  `(defgate ~n [+<- +<+] ~@body))

(defmacro defg3 [n & body]
  `(defgate ~n [+<- +<+< +<+>] ~@body))

(defg1 bex math/bex)
(defg1 inc math/inc)
(defg1 dec math/dec)
(defg2 add math/add)
(defg2 sub math/sub)
(defg2 lth math/lth)
(defg2 gth math/gth)
(defg2 dor math/dor)
(defg2 gor math/gor)
(defg2 vor math/vor)

(defg1 cap bits/cap)
(defg1 mas bits/mas)
(defg2 met bits/met)
(defg2 con bits/con)
(defg2 mix bits/mix)
(defg3 lsh bits/lsh)
(defg3 rsh bits/rsh)
(defg3 cat bits/cat)
(defg3 end bits/end)
(defgate cut [+<- +<+<- +<+<+ +<+>] bits/cut)

(defg1 mat pack/mat)
(defg1 cue pack/cue)
(defg1 jam pack/jam)
(defg2 rub pack/rub)

(defg1 mug  hash/mug)
(defg1 shax hash/shax)
(defg1 sham hash/sham)
(defg2 shay hash/shay)
(defg2 shas hash/shas)
(defg2 shaf hash/shaf)

(defjet by-get [get by hoon mood k151] - [+>+< +<] nlr/by-get)
(defjet by-put [put by hoon mood k151] - [+>+< +<- +<+] nlr/by-put)
