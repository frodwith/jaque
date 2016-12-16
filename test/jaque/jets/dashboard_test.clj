(ns jaque.jets.dashboard-test
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.jets.dashboard :refer :all]
            [jaque.jets.v2 :refer [by-put]]
            [jaque.noun.box :refer :all]
            [jaque.noun.motes :refer [defmote]]
            [jaque.constants :refer :all]
            [clojure.test :refer :all]))

(deftest skip-hints-test
  (is (= (noun [1 10 9 0 1]) (skip-hints (noun [10 1 10 [2 1 1] 10 3 1 10 9 0 1])))))

(deftest hook-axis-test
  (is (= (atom 14) (hook-axis (noun [9 14 0 1]))))
  (is (= (atom 7)  (hook-axis (noun [0 7]))))
  (is (= (atom 2)  (hook-axis (noun [10 1 9 2 0 1])))))

(deftest hot-names-test
  (is (= (noun {2  :add,
                14 :sub, 
                4  :barfbag})
         (hot-names (noun {:add     [9 2 0 1],
                           :sub     [0 14],
                           :wrong   [10 11 9 16 0 6],
                           :barfbag [10 12 9 4 0 1]})))))

(deftest jet-sham-test
  (is (= (atom 0xb24903ca4f712e271f8c3a5d0402cbaa)
         (jet-sham (noun [:add 7 no 151])))))

(deftest clue-list->map-test
  (is (= a0 (clue-list->map a0)))
  (is (= nil (clue-list->map (noun [[[0 0] [0 12]] 0]))))
  (is (= (noun {:add [9 2 0 1],
                :sub [0 14]})
         (clue-list->map (noun (list [:add 9 2 0 1]
                                     [:sub 0 14]))))))

(deftest clue-parent-axis-test
  (is (= a0  (clue-parent-axis (noun [1 0]))))
  (is (= nil (clue-parent-axis (noun [9 2 0 1]))))
  (is (= a7  (clue-parent-axis (noun [0 7]))))
  (is (= (atom 13) (clue-parent-axis (noun [0 13])))))

(defmote fast)

(deftest chum-test
  (is (= a7 (chum a7)))
  (is (= %fast (chum %fast)))
  (is (= (string->cord "fast13") (chum (noun [%fast 13]))))
  (is (= nil (chum (noun [%fast 80835984791392327435748572173984745723874247])))))

(deftest fsck-test
  (is (= nil (fsck (noun [%fast 3 0]))))
  (is (= [%fast a3 a0] (fsck (noun [%fast [0 3] 0]))))
  (is (= [%fast a3 (noun {:foo [9 4 0 1],
                          :bar [9 5 0 1]})]
         (fsck (noun [%fast [0 3] (list [:foo [9 4 0 1]]
                                        [:bar [9 5 0 1]])])))))

(def test-kernel (noun
  [[[7 
      [8 [1 0] [1 [6 [5 [0 7] 4 0 6] [0 6] 9 2 [0 2] [4 0 6] 0 7]] 9 2 0 1]
      [10 [:fast 1 :dec [0 7] 0] 0 1]]
    [7
      [8 [1 0 0] [1 6 [5 [1 0] 0 12] [0 13] 9 2 [0 2] [[8 [9 4 0 7] 9 2 [0 4] [0 28] 0 11] 4 0 13] 0 7] 0 1]
      [10 [:fast 1 :add [0 7] 0] 0 1]]]
   [1 151]
   151]))
