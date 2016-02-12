(ns cljnock.bit-test
  (:refer-clojure :exclude [cat])
  (:require [clojure.test :refer :all]
            [cljnock.bit :refer :all]))

(deftest bex-test
  (testing "bex"
    (is (= (bex 4) 16))
    (is (= (bex 20) 1048576))
    (is (= (bex 0) 1))))

(deftest lsh-test
  (testing "lsh"
    (is (= (lsh 0 1 1) 2))
    (is (= (lsh 3 1 255) 65280))))

(deftest rsh-test
  (testing "rsh"
    (is (= (rsh 1 1 145) 36))
    (is (= (rsh 2 1 145) 9))
    (is (= (rsh 0 1 10) 5))))

(deftest mix-test
  (testing "mix"
    (is (= (mix 2 3) 1)
        (= (mix 2 2) 0))))

(deftest met-test
  (testing "met"
    (is (= (met 0 1) 1))
    (is (= (met 0 2) 2))
    (is (= (met 3 255) 1))
    (is (= (met 3 256) 2))))

(deftest end-test
  (testing "end"
    (is (= (end 0 3 12) 4))
    (is (= (end 1 3 12) 12))))

(deftest cat-test
  (testing "cat"
    (is (= (cat 3 1 0) 1))
    (is (= (cat 0 1 1) 3))
    (is (= (cat 0 2 1) 6))
    (is (= (cat 2 1 1) 17))
    (is (= (cat 3 256 255) 16711936))))

(deftest cut-test
  (testing "cut"
    (is (= (cut 0 [1 1] 2) 1))
    (is (= (cut 0 [2 1] 4) 1))
    (is (= (cut 0 [0 3] 0xf0d) 5))
    (is (= (cut 0 [0 6] 0xf0d) 13))
    (is (= (cut 0 [4 6] 0xf0d) 0x30))
    (is (= (cut 0 [3 6] 0xf0d) 0x21))))
