(ns jaque.vm
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.interpreter :refer :all]
            [jaque.constants   :refer :all]
            [jaque.noun.box    :refer :all]
            [jaque.noun.read   :refer :all]
            [jaque.noun.pack   :refer [cue]])
  (:import jaque.noun.Atom))

;(def decrement (noun [8 [1 0] 8 [1 6 [5 [0 7] 4 0 6] [0 6] 9 2 [0 2] [4 0 6] 0 7] 9 2 0 1]))

(defn slurp-bytes [path]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (clojure.java.io/copy (clojure.java.io/input-stream path) out)
    (.toByteArray out)))

(defn load-pill [path]
  (let [b (slurp-bytes path)
        _ (prn "slurped")
        a (Atom/fromByteArray b Atom/LITTLE_ENDIAN)
        _ (prn "made atom")
        n (cue a)]
    (prn "cued")
    n))

(defn -main []
  (let [pas "/tmp/urbit.pill"
        sys (load-pill pas)
        [m ken]  (nock empty-machine a0 (head sys))
        _        (prn "produced kernel core")
        [m wish] (nock m ken (noun [9 20 0 1]))
        _        (prn "produced wish core")
        [m form] (nock m wish (noun [9 2 [0 2] [1 (string->cord "(add 2 2)")] [0 7]]))
        _        (prn "called wish")
        [m r]    (nock m ken form)]
    (prn "2+2=" r)))

;(defn -main []
;  (println "Uh, hi."))
