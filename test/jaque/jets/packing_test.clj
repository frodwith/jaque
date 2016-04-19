(ns jaque.jets.packing-test
  (:refer-clojure :exclude [atom])
  (:import  (jaque.noun Atom))
  (:require [jaque.noun :refer [cell atom]]
            [jaque.jets.packing :refer :all]
            [clojure.test :refer :all]))


(defn c [p q] (cell (atom p) (atom q)))

(def cued-jammed [[(atom 1)  12]
                  [(c 1 1)   817]
                  [(c 1 2)  4657]
                  [(c 0 19) 39689]
                  [(cell (cell (atom 1) (c 13 14))
                         (cell (c 13 14) (atom 1)))
                   880218685981125]])

(deftest jam-test
  (testing "jam"
    (doseq [[p q] cued-jammed]
      (is (= (jam p) (atom q)) p))))

(deftest mat-test
  (testing "mat"
    (is (= (c 20 699024) (mat (atom 0xaaa))))
    (is (= (c 3 6) (mat (atom 1))))
    (is (= (c 6 36) (mat (atom 2))))))

(deftest rub-test
  (testing "rub"
    (is (= (rub (atom 1) (jam (atom 0xaaa)))
           (c 20 0xaaa)))))

(deftest cue-test
  (testing "cue"
    (doseq [[p q] cued-jammed]
      (is (= (cue (atom q)) p) q))))
