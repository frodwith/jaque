(ns jaque.jets.math
  (:refer-clojure :exclude [inc dec cat])
  (:import [jaque.noun Atom])
  (:use [jaque.jets]))

(set! *warn-on-reflection* true)

(defj add [a b] (.add a b))
(defj sub [a b] (.sub a b))
(defj inc [a]   (.add a Atom/ONE))
(defj dec [a]   (.sub a Atom/ONE))
(defj lth [a b] (= -1 (.compareTo a b)))
(defj lte [a b] (not= 1 (.compareTo a b)))
(defj gth [a b] (= 1 (.compareTo a b)))
(defj gte [a b] (not= -1 (.compareTo a b)))
