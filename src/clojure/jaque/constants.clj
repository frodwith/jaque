(ns jaque.constants
  (:import (jaque.noun Atom)))

(doseq [i (range 0 11)]
  (intern *ns* (symbol (str "a" i)) (Atom/fromLong i)))

(def yes a0)
(def no  a1)

