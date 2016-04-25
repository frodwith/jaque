(ns jaque.vm
  (:refer-clojure :exclude [atom])
  (:require [jaque.jets.packing :refer [cue]]
            [jaque.noun :refer :all]
            [jaque.nock :refer [phi]])
  (:import jaque.noun.Atom))

(def decrement (noun [8 [1 0] 8 [1 6 [5 [0 7] 4 0 6] [0 6] 9 2 [0 2] [4 0 6] 0 7] 9 2 0 1]))

(defn slurp-bytes [path]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (clojure.java.io/copy (clojure.java.io/input-stream path) out)
    (.toByteArray out)))

(defn load-pill [path]
  (cue (Atom/fromByteArray (slurp-bytes path) Atom/LITTLE_ENDIAN)))

(defn -main []
  (let [pas "/home/pdriver/piers/urbit.pill"
        sys (load-pill pas)
        bin (phi (hed sys))]
    (println "We have compiled the kernel.")))
