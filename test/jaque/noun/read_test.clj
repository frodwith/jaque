(ns jaque.noun.read-test
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.noun.read :refer :all]
            [jaque.noun.box :refer [cell atom noun]]
            [jaque.constants :refer [a0 a1 a2 a3 yes no]]
            [slingshot.test :refer :all]
            [clojure.test :refer :all]))

(deftest test-atom?
  (is (= false (atom? 0)))
  (is (= false (atom? (cell a0 a0))))
  (is (= false (atom? "hi")))
  (is (= true  (atom? a0))))

(deftest test-cell?
  (is (= false (cell? 0)))
  (is (= true  (cell? (cell a0 a0))))
  (is (= false (cell? "hi")))
  (is (= false (cell? a0))))

(deftest test-zero?
  (is (= false (zero? (cell a0 a0))))
  (is (= false (zero? a1)))
  (is (= true  (zero? a0))))

(deftest test-noun?
  (is (= false (noun? 0)))
  (is (= true  (noun? (cell a0 a0))))
  (is (= false (noun? "hi")))
  (is (= true  (noun? a0))))

(deftest test-cord->string
  (is (= "fast"  (cord->string (atom 1953718630))))
  (is (= "hello" (cord->string (atom 478560413032))))
  (is (= "nock"  (cord->string (atom 1801678702)))))

(deftest test-head
  (is (= a1 (head (cell a1 a2)))))

(deftest test-tail
  (is (= a2 (tail (cell a1 a2)))))

(deftest test-mean
  (let [x (cell a1 a2 a3)]
    (is (= (list a1 a3) (mean x (atom 2) (atom 7))))
    (is (= (list a3 a2) (mean x (atom 7) (atom 6))))
    (is (= (list a1 a2 a3) (mean x (atom 2) (atom 6) (atom 7))))
    (is (= (list a1) (mean x (atom 2))))
    (is (= (list) (mean x)))))

(deftest test-fragment
  (let [x (cell a0 a1 a2 a3)]
    (is (= a0 (fragment (atom 2) x)))
    (is (= a1 (fragment (atom 6) x)))
    (is (= a2 (fragment (atom 14) x)))
    (is (= a3 (fragment (atom 15) x)))))

(deftest test-lark->axis
  (is (= (atom 0) (lark->axis "tomfoolery")))
  (is (= (atom 2) (lark->axis "-")))
  (is (= (atom 3) (lark->axis "+")))
  (is (= (atom 4) (lark->axis "-<")))
  (is (= (atom 5) (lark->axis "->")))
  (is (= (atom 6) (lark->axis "+<")))
  (is (= (atom 7) (lark->axis "+>")))
  (is (= (atom 8) (lark->axis "-<-")))
  (is (= (atom 9) (lark->axis "-<+"))))

(deftest test-lark
  (let [x (cell a0 a1 a2 a3)]
    (is (= a0 (lark - x)))
    (is (= a1 (lark +< x)))
    (is (= a2 (lark +>- x)))
    (is (= a3 (lark +>+ x)))
    (is (thrown? ClassCastException (lark +>+> x)))))

(deftest test-try-lark
  (let [x (cell a0 a1 a2 a3)]
    (is (= a0 (try-lark - x)))
    (is (= a1 (try-lark +< x)))
    (is (= a2 (try-lark +>- x)))
    (is (= a3 (try-lark +>+ x)))
    (is (= nil (try-lark +>+> x)))))

(deftest test-if&
  (is (= true  (if& yes true false)))
  (is (= false (if& no  true false)))
  (is (thrown+? 
        [:type :jaque.error/bail :bail-type :exit]
        (if& true true false))))

(deftest test-if|
  (is (= false (if| yes true false)))
  (is (= true  (if| no  true false)))
  (is (thrown+? 
        [:type :jaque.error/bail :bail-type :exit]
        (if| true true false))))

(deftest trel-seq-test
  (is (= [a1 a2 a3] (trel-seq (noun [1 2 3]))))
  (is (= [(cell a1 a1) a2 a3] (trel-seq (noun [[1 1] 2 3]))))
  (is (= [a1 (cell a2 a2) a3] (trel-seq (noun [1 [2 2] 3]))))
  (is (= [a1 a2 (cell a3 a3)] (trel-seq (noun [1 2 3 3])))))

(deftest nlr-seq-test
  (is (= [(noun [97 1])] 
         (nlr-seq (noun [[97 1] 0 0]))))
  (is (= [(noun [97 1]) (noun [98 2])] 
         (nlr-seq (noun [[98 2] [[97 1] 0 0] 0]))))
  (is (= [(noun [7171949 31337])] 
         (nlr-seq (noun [[7171949 31337] 0 0]))))
  (is (= [(noun [7171949 31337]) (noun [26984 42])] 
         (nlr-seq (noun [[7171949 31337] 0 [26984 42] 0 0])))))
