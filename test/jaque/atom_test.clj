(ns jaque.atom-test
  (:import jaque.noun.Atom)
  (:refer-clojure :exclude [dec inc zero? cat])
  (:require [clojure.test :refer :all]
            [jaque.atom :refer :all]))

(defn a [l] (Atom/fromLong l))

(deftest bex-test
  (testing "bex"
    (is (= (bex 4) (a 16)))
    (is (= (bex 20) (a 1048576)))
    (is (= (bex 0) one))))

(deftest lsh-test
  (testing "lsh"
    (is (= (lsh 0 1 (a 1)) (a 2)))
    (is (= (lsh 3 1 (a 255)) (a 65280)))
    (is (= (lsh 0 2 (a 2r100)) (a 2r10000)))
    ; rsh accounts for the sign bit
    (is (= (rsh 0 1 (lsh 3 8 one)) (inc (Atom/fromLong Long/MAX_VALUE))))))

(deftest rsh-test
  (testing "rsh"
    (is (= (rsh 1 1 (a 145)) (a 36)))
    (is (= (rsh 2 1 (a 145)) (a 9)))
    (is (= (rsh 0 1 (a 10)) (a 5)))))

(deftest mix-test
  (testing "mix"
    (is (= (mix (a 2) (a 3)) one)
        (= (mix (a 2) (a 2)) zero))))

(deftest met-test
  (testing "met"
    (is (= (met 0 (a 1)) 1))
    (is (= (met 0 (a 2)) 2))
    (is (= (met 3 (a 255)) 1))
    (is (= (met 3 (a 256)) 2))
    (is (= (met 3 (lsh 3 9 (a 1))) 10))
    (is (= (met 6 (lsh 0 63 (a 1))) 1))))

(deftest end-test
  (testing "end"
    (is (= (end 0 (a 3) (a 12)) (a 4)))
    (is (= (end 1 (a 3) (a 12)) (a 12)))))

(deftest cat-test
  (testing "cat"
    (is (= (cat 3 one zero) one))
    (is (= (cat 0 one one) (a 3)))
    (is (= (cat 0 (a 2) one) (a 6)))
    (is (= (cat 2 one one) (a 17)))
    (is (= (cat 3 (a 256) (a 255)) (a 16711936)))))

(deftest cut-test
  (testing "cut"
    (is (= (cut 0 (a 1) (a 1) (a 2)) one))
    (is (= (cut 0 (a 2) (a 1) (a 4)) one))
    (is (= (cut 0 (a 0) (a 3) (a 0xf0d)) (a 5)))
    (is (= (cut 0 (a 0) (a 6) (a 0xf0d)) (a 13)))
    (is (= (cut 0 (a 4) (a 6) (a 0xf0d)) (a 0x30)))
    (is (= (cut 0 (a 3) (a 6) (a 0xf0d)) (a 0x21)))))
