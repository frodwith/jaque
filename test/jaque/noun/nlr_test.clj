(ns jaque.noun.nlr-test
  (:refer-clojure :exclude [atom])
  (:require [jaque.noun.nlr :refer :all]
            [jaque.constants :refer :all]
            [jaque.noun.box :refer :all]
            [clojure.test :refer :all]))

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

(deftest nlr-seq-test
  (is (= [(noun [97 1])]
         (nlr-seq (noun [[97 1] 0 0]))))
  (is (= [(noun [97 1]) (noun [98 2])]
         (nlr-seq (noun [[98 2] [[97 1] 0 0] 0]))))
  (is (= [(noun [7171949 31337])]
         (nlr-seq (noun [[7171949 31337] 0 0]))))
  (is (= [(noun [7171949 31337]) (noun [26984 42])]
         (nlr-seq (noun [[7171949 31337] 0 [26984 42] 0 0])))))
