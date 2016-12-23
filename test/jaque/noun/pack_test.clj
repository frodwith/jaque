(ns jaque.noun.pack-test
  (:refer-clojure :exclude [atom])
  (:require [jaque.noun.pack :refer :all]
            [jaque.constants :refer :all]
            [jaque.noun.box :refer :all]
            [clojure.test :refer :all]))

(deftest jam-test
  (is (= (atom 817)             (jam (noun [1 1]))))
  (is (= (atom 4657)            (jam (noun [1 2]))))
  (is (= (atom 39689)           (jam (noun [0 19]))))
  (is (= (atom 880218685981125) (jam (noun [[1 [13 14]] [13 14] 1])))))

(deftest mat-test
  (is (= (noun [20 699024]) (mat (atom 0xaaa))))
  (is (= (noun [3 6])       (mat a1)))
  (is (= (noun [6 36])      (mat a2))))

(deftest rub-test
  (is (= (rub a1 (jam (atom 0xaaa))))))

(deftest cue-test
  (is (= (noun [1 1])                   (cue (atom 817))))
  (is (= (noun [1 2])                   (cue (atom 4657))))
  (is (= (noun [0 19])                  (cue (atom 39689))))
  (is (= (noun [[1 [13 14]] [13 14] 1]) (cue (atom 880218685981125)))))
