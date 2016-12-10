(ns jaque.machine-test
  (:refer-clojure :exclude [atom])
  (:require [jaque.machine :refer :all]
            [jaque.noun.box :refer :all]
            [clojure.test :refer :all]))

(deftest hook-axis-test
  (is (= (atom 14) (hook-axis (noun [9 14 0 1]))))
  (is (= (atom 7)  (hook-axis (noun [0 7]))))
  (is (= (atom 2)  (hook-axis (noun [10 1 9 2 0 1])))))
