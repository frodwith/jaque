(ns jaque.jets.v2-test
  (:refer-clojure :exclude [atom])
  (:require [jaque.jets.v2 :refer :all]
            [jaque.constants :refer :all]
            [jaque.noun.box :refer :all]
            [clojure.test :refer :all]))

(deftest bex-test
  (is (= (bex a0) a1))
  (is (= (bex a1) a2))
  (is (= (bex a2) (atom 4)))
  (is (= (bex a3) (atom 8)))
  (is (= (bex (atom 42)) (atom 4398046511104))))

(deftest lth-test
  (is (= yes (lth a0 a1)))
  (is (= no  (lth a1 a1)))
  (is (= no  (lth a1 a0)))
  (is (= no  (lth a10 a0)))
  (is (= yes (lth a3 a10))))

(deftest mug-test
  (is (= (atom 1097343833) (mug (noun [0 0]))))
  (is (= (atom 669157133)  (mug (noun [0 0 0]))))
  (is (= (atom 763078933)  (mug (noun [[69 42] 31337]))))
  (is (= (atom 18652612)   (mug a0))))

(deftest dor-test
  (is (= yes (dor (noun 1)       (noun [0 0]))))
  (is (= yes (dor (noun [0 1])   (noun [1 0]))))
  (is (= no  (dor (noun 84)      (noun 82))))
  (is (= yes (dor (noun 42)      (noun [42 42]))))
  (is (= no  (dor (noun [42 43]) (noun [42 42])))))

(deftest gor-test
  (is (= yes (gor (noun 1)       (noun [0 0]))))
  (is (= no  (gor (noun [0 1])   (noun [1 0]))))
  (is (= yes (gor (noun 84)      (noun 82))))
  (is (= yes (gor (noun 42)      (noun [42 42]))))
  (is (= yes (gor (noun [42 43]) (noun [42 42])))))

(deftest vor-test
  (is (= yes (vor a0 a1)))
  (is (= no  (vor a1 a0)))
  (is (= no  (vor a3 a10)))
  (is (= yes (vor a10 a2)))
  (is (= no  (vor a3 a0))))

(deftest by-get-test
  (let [a (noun [[98 2] [[97 1] 0 0] 0])
        b (noun [[7171949 31337] 0 [26984 42] 0 0])]
    (is (= (by-get a (atom 97))      (noun [0 1])))
    (is (= (by-get a (atom 98))      (noun [0 2])))
    (is (= (by-get a (atom 99))      a0))
    (is (= (by-get b (atom 7171949)) (noun [0 31337])))
    (is (= (by-get b (atom 26984))   (noun [0 42])))
    (is (= (by-get b (atom 42))      a0))))

(deftest by-put-test
  (let [a (by-put a0 (atom 97)      (atom 1))
        b (by-put a  (atom 98)      (atom 2))
        c (by-put a0 (atom 7171949) (atom 31337))
        d (by-put c  (atom 26984)   (atom 42))]
    (is (= a (noun [[97 1] 0 0])))
    (is (= b (noun [[98 2] [[97 1] 0 0] 0])))
    (is (= c (noun [[7171949 31337] 0 0])))
    (is (= d (noun [[7171949 31337] 0 [26984 42] 0 0])))))

(deftest cap-test
  (is (= a2 (cap (atom 4))))
  (is (= a3 (cap (atom 6))))
  (is (= a2 (cap (atom 16))))
  (is (= a3 (cap (atom 15)))))

(deftest con-test
  (is (= (atom 30)             (con (atom 20)            (atom 30))))
  (is (= (atom 103)            (con (atom 99)            (atom 100))))
  (is (= (atom 111)            (con (atom 42)            (atom 69))))
  (is (= (atom 13191442432894) (con (atom 4291984392492) (atom 9321673753214)))))

(deftest lsh-test
  (is (= (atom 12)       (lsh a0 a1 (atom 6))))
  (is (= (atom 512)      (lsh a2 a2 a2)))
  (is (= (atom 160)      (lsh a1 a2 a10)))
  (is (= (atom 50331648) (lsh a3 a3 a3))))

(deftest mas-test
  (is (= a2       (mas (atom 4))))
  (is (= a2       (mas (atom 6))))
  (is (= (atom 8) (mas (atom 16))))
  (is (= (atom 7) (mas (atom 15)))))

(deftest sub-test
  (is (= a1 (sub a3 a2)))
  (is (= a1 (sub a2 a1)))
  (is (= a3 (sub (atom 10) (atom 7))))
  (is (= (atom 27) (sub (atom 69) (atom 42)))))
