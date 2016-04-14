(ns jaque.atom
  (:refer-clojure :exclude [zero? inc dec cat])
  (:import jaque.noun.Atom))

(set! *warn-on-reflection* true)

(defn zero? [^Atom a] (.isZero a))

(def ^Atom zero  Atom/ZERO)
(def ^Atom one   Atom/ONE)
(def ^Atom two   Atom/TWO)
(def ^Atom three Atom/THREE)
(def ^Atom ten   Atom/TEN)

(defn rsh ^Atom [a b ^Atom c] (.rsh c a b))
(defn lsh ^Atom [a b ^Atom c] (.lsh c a b))

(defn add ^Atom [^Atom a ^Atom b] (.add a b))
(defn sub ^Atom [^Atom a ^Atom b] (.sub a b))
(defn inc ^Atom [a] (add a one))
(defn dec ^Atom [a] (sub a one))

(defn lth [^Atom a ^Atom b] (= -1 (.compareTo a b)))
(defn lte [^Atom a ^Atom b] (not= 1 (.compareTo a b)))
(defn gth [^Atom a ^Atom b] (= 1 (.compareTo a b)))
(defn gte [^Atom a ^Atom b] (not= -1 (.compareTo a b)))

(defn met ^long [a ^Atom b] (.met b a))
(defn cut ^Atom [a ^Atom b ^Atom c ^Atom d] (.cut d a b c))
(defn end ^Atom [a ^Atom b ^Atom c] (.end c a b))
(defn cat ^Atom [a ^Atom b ^Atom c] (Atom/cat a b c))
(defn mix ^Atom [^Atom a ^Atom b] (.mix a b))

(def bex (memoize (fn ^Atom [n] (lsh 0 n one))))
