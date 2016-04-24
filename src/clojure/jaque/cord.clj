(ns jaque.cord
  (:refer-clojure :exclude [atom])
  (:import (jaque.noun Atom)))

(defn cord->string ^String [^Atom c]
  (String. (.toByteArray c Atom/LITTLE_ENDIAN) "UTF-8"))

(defn string->cord ^Atom [^String s]
  (Atom/fromByteArray (.getBytes s "UTF-8") Atom/LITTLE_ENDIAN))
