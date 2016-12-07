(ns jaque.noun.box-test
  (:refer-clojure :exclude [atom])
  (:require [jaque.noun.box :refer :all]
            [jaque.constants :refer :all]
            [jaque.noun.read :refer [atom? cell? noun? lark]]
            [clojure.test :refer :all]))

(deftest atom-test
  (is (= true (atom? (atom 42))))
  (is (= true (atom? (atom "42"))))
  (is (= (atom 42) (atom "42")))
  (is (= true (atom? (atom 10000000000))))
  (is (= true (atom? (atom "10000000000"))))
  (is (= (atom 10000000000) (atom 10000000000))))

(deftest cell-test
  (is (= true (cell? (cell a1 a2))))
  (is (= a2 (lark +>- (cell a0 a1 a2 a3)))))

(deftest noun-test
  (is (= (atom 31337) (noun 31337)))
  (is (= (cell a0 (cell a1 a2) a3) (noun [0 [1 2] 3]))))

(deftest test-string->cord
  (is (= (string->cord "fast") (atom 1953718630)))
  (is (= (string->cord "hello") (atom 478560413032)))
  (is (= (string->cord "nock") (atom 1801678702))))
