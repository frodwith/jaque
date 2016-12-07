(ns jaque.noun.print
  (:import (jaque.noun Atom)))

(defmethod print-dup jaque.noun.Atom [o w]
  (print-ctor o (fn [o w]
                  (print-dup (if (.isCat o) 
                               (.intValue o)
                               (.toString o))
                             w)) w))
