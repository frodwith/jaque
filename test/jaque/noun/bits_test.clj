(ns jaque.noun.bits-test
  (:refer-clojure :exclude [cat atom])
  (:require [jaque.constants :refer :all]
            [jaque.noun.box  :refer :all]
            [jaque.noun.bits :refer :all]
            [clojure.test :refer :all]))

(deftest lsh-test
  (is (= (atom 12)       (lsh a0 a1 (atom 6))))
  (is (= (atom 512)      (lsh a2 a2 a2)))
  (is (= (atom 160)      (lsh a1 a2 a10)))
  (is (= (atom 50331648) (lsh a3 a3 a3))))

(deftest met-test
  (is (= a1 (met a0 a1)))
  (is (= a7 (met a2 (atom 123456789))))
  (is (= (atom 39) (met a1 (atom 100000000000000000000000))))
  (is (= a3 (met a5 (atom 100000000000000000000000)))))

(deftest end-test
  (is (= a4           (end a0 a3 (atom 12))))
  (is (= (atom 12)    (end a1 a3 (atom 12))))
  (is (= (atom 62395) (end a0 (atom 16) (atom 65692431291)))))

(deftest mix-test
  (is (= a1                 (mix a2 a3)))
  (is (= a0                 (mix a2 a2)))
  (is (= (atom 3073)        (mix (atom 3947) (atom 874))))
  (is (= (atom 65692368896) (mix (atom 62395) (atom 65692431291)))))

(deftest cut-test
  (is (= a1          (cut a0 a1 a1 a2)))
  (is (= a1          (cut a0 a2 a1 a4)))
  (is (= a5          (cut a0 a0 a3 (atom 0xf0d))))
  (is (= (atom 13)   (cut a0 a0 a6 (atom 0xf0d))))
  (is (= (atom 0x30) (cut a0 a4 a6 (atom 0xf0d))))
  (is (= (atom 0x21) (cut a0 a3 a6 (atom 0xf0d)))))

(deftest cat-test
  (is (= a1              (cat a3 a1 a0)))
  (is (= a3              (cat a0 a1 a1)))
  (is (= a6              (cat a0 a2 a1)))
  (is (= (atom 17)       (cat a2 a1 a1)))
  (is (= (atom 16711936) (cat a3 (atom 256) (atom 255)))))

(deftest mas-test
  (is (= a2       (mas (atom 4))))
  (is (= a2       (mas (atom 6))))
  (is (= (atom 8) (mas (atom 16))))
  (is (= (atom 7) (mas (atom 15)))))

(deftest cap-test
  (is (= a2 (cap (atom 4))))
  (is (= a3 (cap (atom 6))))
  (is (= a2 (cap (atom 16))))
  (is (= a3 (cap (atom 15)))))

(deftest rsh-test
  (is (= (atom 36)     (rsh a1 a1 (atom 145))))
  (is (= a9            (rsh a2 a1 (atom 145))))
  (is (= a5            (rsh a0 a1 a10)))
  (is (= (atom 860002) (rsh a0 (atom 18) (atom 225444443422)))))

(deftest con-test
  (is (= (atom 30)             (con (atom 20)            (atom 30))))
  (is (= (atom 103)            (con (atom 99)            (atom 100))))
  (is (= (atom 111)            (con (atom 42)            (atom 69))))
  (is (= (atom 13191442432894) (con (atom 4291984392492) (atom 9321673753214)))))
