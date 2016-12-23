(ns jaque.noun.hash
  (:require [jaque.noun.box  :refer [noun]]
            [jaque.noun.read :refer [atom?]]
            [jaque.noun.core :refer [export]]
            [jaque.noun.pack :refer [jam]]
            [jaque.noun.bits :refer [end met mix rsh]]
            [jaque.constants :refer :all])
  (:import jaque.noun.Atom
           java.util.Arrays
           java.security.MessageDigest))

(export mug)

(defn shay [^Atom len ^Atom ruz]
  (let [dig (MessageDigest/getInstance "SHA-256")
        len (.intValue len)
        byt (.toByteArray ruz Atom/LITTLE_ENDIAN)
        byt (Arrays/copyOfRange byt 0 len)
        h   (.digest dig byt)]
    (Atom/fromByteArray h Atom/LITTLE_ENDIAN)))

(defn shax [ruz]
  (shay (met a3 ruz) ruz))

(defn shas [sal ruz]
  (shax (mix sal (shax ruz))))

(defn shaf [sal ruz]
  (let [haz (shas sal ruz)]
    (mix (end a7 a1 haz) (rsh a7 a1 haz))))

(defn sham [yux]
  (if (atom? yux)
    (shaf (noun :mash) yux)
    (shaf (noun :sham) (jam yux))))
