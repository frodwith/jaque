(ns jaque.cord
  (:refer-clojure :exclude [atom])
  (:import (jaque.noun Atom)))

(defn cord->string ^String [^Atom c]
  (String. (.toLittleEndian c) "UTF-8"))

(defn string->cord ^Atom [^String s]
  (Atom/fromPill (.getBytes s "UTF-8")))
