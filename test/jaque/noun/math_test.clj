(ns jaque.noun.math-test
  (:refer-clojure :exclude [dec inc atom])
  (:require [jaque.constants :refer :all]
            [jaque.noun.box :refer :all]
            [jaque.noun.math :refer :all]
            [clojure.test :refer :all]))

(deftest add-test
  (is (= a4 (add a2 a2)))
  (is (= a7 (add a4 a3)))
  (is (= (atom 111) (add (atom 69) (atom 42))))
  (is (= (atom 225444443422) (add (atom 123456789101) (atom 101987654321)))))

(deftest dec-test
  (is (= a0  (dec a1)))
  (is (= a1  (dec a2)))
  (is (= a9  (dec a10)))
  (is (= (atom 225444443422) (dec (atom 225444443423)))))

(deftest vor-test
  (is (= yes (vor a0 a1)))
  (is (= no  (vor a1 a0)))
  (is (= no  (vor a3 a10)))
  (is (= yes (vor a10 a2)))
  (is (= no  (vor a3 a0))))

(deftest bex-test
  (is (= (bex a0) a1))
  (is (= (bex a1) a2))
  (is (= (bex a2) (atom 4)))
  (is (= (bex a3) (atom 8)))
  (is (= (bex (atom 42)) (atom 4398046511104))))

(deftest gor-test
  (is (= yes (gor (noun 1)       (noun [0 0]))))
  (is (= no  (gor (noun [0 1])   (noun [1 0]))))
  (is (= yes (gor (noun 84)      (noun 82))))
  (is (= yes (gor (noun 42)      (noun [42 42]))))
  (is (= yes (gor (noun [42 43]) (noun [42 42])))))

(deftest sub-test
  (is (= a1 (sub a3 a2)))
  (is (= a1 (sub a2 a1)))
  (is (= a3 (sub (atom 10) (atom 7))))
  (is (= (atom 27) (sub (atom 69) (atom 42)))))

(deftest gth-test
  (is (= no  (gth a0 a1)))
  (is (= no  (gth a1 a1)))
  (is (= yes (gth a1 a0)))
  (is (= yes (gth a10 a0)))
  (is (= no  (gth a3 a10))))

(deftest inc-test
  (is (= a1  (inc a0)))
  (is (= a2  (inc a1)))
  (is (= a10 (inc a9)))
  (is (= (atom 225444443423) (inc (atom 225444443422)))))

(deftest dor-test
  (is (= yes (dor (noun 1)       (noun [0 0]))))
  (is (= yes (dor (noun [0 1])   (noun [1 0]))))
  (is (= no  (dor (noun 84)      (noun 82))))
  (is (= yes (dor (noun 42)      (noun [42 42]))))
  (is (= no  (dor (noun [42 43]) (noun [42 42])))))

(deftest lth-test
  (is (= yes (lth a0 a1)))
  (is (= no  (lth a1 a1)))
  (is (= no  (lth a1 a0)))
  (is (= no  (lth a10 a0)))
  (is (= yes (lth a3 a10))))
