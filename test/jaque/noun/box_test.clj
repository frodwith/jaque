(ns jaque.noun.box-test
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.noun.box :refer :all]
            [jaque.constants :refer :all]
            [jaque.noun.read :refer :all]
            [clojure.test :refer :all]))

(deftest atom-test
  (is (= true (atom? (atom 42))))
  (is (= true (atom? (atom "42"))))
  (is (= (atom 42) (atom "42")))
  (is (= true (atom? (atom 10000000000))))
  (is (= true (atom? (atom "10000000000"))))
  (is (= (int \f) (.intValue (atom \f))))
  (is (= (atom 10000000000) (atom 10000000000))))

(deftest cell-test
  (is (= true (cell? (cell a1 a2))))
  (is (= a2 (lark +>- (cell a0 a1 a2 a3)))))

(deftest test-string->cord
  (is (= (string->cord "fast") (atom 1953718630)))
  (is (= (string->cord "hello") (atom 478560413032)))
  (is (= (string->cord "nock") (atom 1801678702))))

(deftest seq->it-test
  (is (= (cell a1 a2 a3 a0) (seq->it (list 1 2 3)))))

(deftest map->nlr-test
  (is (= (noun [[98 2] [[97 1] 0 0] 0])
         (map->nlr {:a 1, :b 2}))))

(deftest noun-test
  (is (= "foo" (cord->string (noun :foo))))
  (is (= (atom (int \f)) (noun \f)))
  (is (= (cell a1 a2 a3 a0) (noun (list 1 2 3))))
  (is (= (cell (atom \f) (atom \o) (atom \o) a0) (noun "foo")))
  (is (= (atom 31337) (noun 31337)))
  (is (= (map->nlr {:a 1, :b 2}) (noun {:a 1, :b 2})))
  (is (= (seq->it (list 1 2 3)) (noun (list 1 2 3))))
  (is (= (cell a0 (cell a1 a2) a3) (noun [0 [1 2] 3]))))
