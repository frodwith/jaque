(ns jaque.jets.hash
  (:refer-clojure :exclude [atom])
  (:require [jaque.cord :refer [string->cord]] 
            [jaque.noun :refer :all]
            [jaque.jets :refer [defj]]
            [jaque.jets.bit-logic :refer [mix]]
            [jaque.jets.packing :refer [jam]]
            [jaque.jets.bit-surgery :refer [met end rsh]])
  (:import (jaque.noun Atom) 
           (java.util Arrays)
           (java.security MessageDigest)))

(defj shay [len ruz]
  (let [dig (MessageDigest/getInstance "SHA-256")
        len (.intValue len)
        byt (.toByteArray ruz Atom/LITTLE_ENDIAN)
        byt (if (< len (alength byt))
              (Arrays/copyOfRange byt 0 len)
              byt)
        h (.digest dig byt)]
    (Atom/fromByteArray h Atom/LITTLE_ENDIAN)))

(defj shax [ruz]
  (shay (met a3 ruz) ruz))

(defj shas [sal ruz]
  (shax (mix sal (shax ruz))))

(def a7 (atom 7))

(defj shaf [sal ruz]
  (let [haz (shas sal ruz)]
    (mix (end a7 a1 haz) (rsh a7 a1 haz))))

(def mash-mote (string->cord "mash"))
(def sham-mote (string->cord "sham"))

(defj sham [yux]
  (if (atom? yux)
    (shaf mash-mote yux)
    (shaf sham-mote (jam yux))))

(defn mug [^Atom a] (atom (.hashCode a)))
