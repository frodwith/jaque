(ns jaque.wire-test
  (:import  jaque.noun.Atom)
  (:require [jaque.noun :refer [noun]]
            [jaque.wire :refer :all]
            [clojure.test :refer :all]))

(def cued-jammed [[     1    12]
                  [ [1 1]   817]
                  [ [1 2]  4657]
                  [[0 19] 39689]
                  [[[1 [13 14]] [[13 14] 1]]
                   (Atom. "880218685981125")]])

(deftest jam-test
  (testing "jam"
    (doseq [[p q] cued-jammed]
      (is (= (jam (noun p)) q) p))))

(deftest mat-test
  (testing "mat"
    (is (= [20 699024] (mat (noun 0xaaa))))
    (is (= [3 6] (mat (noun 1))))
    (is (= [6 36] (mat (noun 2))))))

(deftest rub-test
  (testing "rub"
    (is (= (rub 1 (jam (noun 0xaaa)))
           [20 (noun 0xaaa)]))))

(deftest cue-test
  (testing "cue"
    (doseq [[p q] cued-jammed]
      (is (= (cue (noun q)) (noun p)) q))))
