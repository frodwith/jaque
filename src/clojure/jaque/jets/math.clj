(ns jaque.jets.math
  (:refer-clojure :exclude [inc dec cat atom])
  (:import (jaque.noun Atom))
  (:require [jaque.jets :refer [defj]]
            [jaque.noun :refer [atom]]))

(defj add [a b] (.add a b))
(defj sub [a b] (.sub a b))
(defj inc [a]   (.add a (atom 1)))
(defj dec [a]   (.sub a (atom 1)))
(defj lth [a b] (= -1 (.compareTo a b)))
(defj lte [a b] (not= 1 (.compareTo a b)))
(defj gth [a b] (= 1 (.compareTo a b)))
(defj gte [a b] (not= -1 (.compareTo a b)))
