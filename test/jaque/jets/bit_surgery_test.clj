(ns jaque.jets.bit-surgery-test
  (:refer-clojure :exclude [cat])
  (:import jaque.noun.Atom)
  (:require [clojure.test :refer :all]
            [jaque.noun :refer [atom?]]
            [jaque.jets.util :refer :all]
            [jaque.jets.bit-surgery :as j]
            [jaque.jets.math        :as m]))

(def bex (atomize j/bex))
(deftest bex-test
  (testing "bex"
    (is (= (bex 4) 16)))
    (is (= (bex 20) 1048576))
    (is (= (bex 0) 1)))

(def lsh (atomize j/lsh))

(deftest lsh-test
  (testing "lsh"
    (is (= (lsh 0 1 1)     2))
    (is (= (lsh 3 1 255)   65280))
    (is (= (lsh 0 2 2r100) 2r10000))
    ; rsh accounts for the sign bit
    (is (= (j/rsh (a 0) (a 1) (j/lsh (a 3) (a 8) (a 1))) (m/inc (Atom/fromLong Long/MAX_VALUE))))))

(def rsh (atomize j/rsh))
(deftest rsh-test
  (testing "rsh"
    (is (= (rsh 1 1 145) 36))
    (is (= (rsh 2 1 145) 9))
    (is (= (rsh 0 1 10) 5))))

(def met (atomize j/met))
(deftest met-test
  (testing "met"
    (is (= (met 0 1) 1))
    (is (= (met 0 2) 2))
    (is (= (met 3 255) 1))
    (is (= (met 3 256) 2))
    (is (= (j/met (a 3) (j/lsh (a 3) (a 9) (a 1))) (a 10)))
    (is (= (j/met (a 6) (j/lsh (a 0) (a 63) (a 1))) (a 1)))))

(def end (atomize j/end))
(deftest end-test
  (testing "end"
    (is (= (end 0 3 12) 4))
    (is (= (end 1 3 12) 12))))

(def cat (atomize j/cat))
(deftest cat-test
  (testing "cat"
    (is (= (cat 3 1 0) 1))
    (is (= (cat 0 1 1) 3))
    (is (= (cat 0 2 1) 6))
    (is (= (cat 2 1 1) 17))
    (is (= (cat 3 256 255) 16711936))))

(def cut (atomize j/cut))
(deftest cut-test
  (testing "cut"
    (is (= (cut 0 1 1 2) 1))
    (is (= (cut 0 2 1 4) 1))
    (is (= (cut 0 0 3 0xf0d) 5))
    (is (= (cut 0 0 6 0xf0d) 13))
    (is (= (cut 0 4 6 0xf0d) 0x30))
    (is (= (cut 0 3 6 0xf0d) 0x21))))
