(ns jaque.constants
  (:import (jaque.noun Atom)))

(doseq [i [0 1 2 3 10]]
  (intern *ns* (symbol (str "a" i)) (Atom/fromLong i)))

(def yes a0)
(def no  a1)

