(ns jaque.jets.util
  (:import jaque.noun.Atom))

(defn a [l] (Atom/fromLong l))

(defn atomize [f]
  (fn [& args] (.longValue (apply f (map a args)))))
