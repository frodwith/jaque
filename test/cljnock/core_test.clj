(ns cljnock.core-test
  (:require [clojure.test :refer :all]
            [cljnock.core :refer :all]))

(deftest sugar
  (testing "vector->noun sugar"
    (doseq [[p q] [[[0 1]       [0 1]]
                   [[0 1 2]     [0 [1 2]]]
                   [[0 [1 2 3]] [0 [1 [2 3]]]]
                   [[[0 1 2] [3 4 5] [6 7 8]]
                    [[0 [1 2]] [[3 [4 5]] [6 [7 8]]]]]
                   ]]
      (is (= (noun p) q) p))))

(deftest leg-test
  (testing "tree-indexing (leg)"
    (let [x (noun [[10 20] [30 40 50 60]])]
      (doseq [[p q] [[0  x]
                     [1  x]
                     [2  (x 0)]
                     [3  (x 1)]
                     [4  10]
                     [5  20]
                     [6  30]
                     [7  (noun [40 50 60])]
                     [8  crash]
                     [9  crash]
                     [10 crash]
                     [11 crash]
                     [12 crash]
                     [13 crash]
                     [14 40]
                     [15 (noun [50 60])]
                     [30 50]
                     [31 60]]]
        (is (= (leg x p) q) p)))))

(deftest tutorial
  (testing "examples from nock tutorial"
    (doseq [[[sub fom] res msg]
            [[[[[4 5] [6 14 15]] [0 7]]
              [14 15]
              "sky blue, sun east"]
             [[42 [1 153 218]]
              [153 218]
              "constant operator"]
             [[77 [2 [1 42] [1 1 153 218]]]
              [153 218]
              "stupid use of 2"]
             [[57 [0 1]]
              57
              "increment"]
             [[[132 19] [0 3]]
              19
              "increment 2"]
             [[57 [4 0 1]]
              58
              "increment 3"]
             [[[132 19] [4 0 3]]
              20
              "increment 4"]
             [[42 [4 0 1]]
              43
              "increment again"]
             [[42 [[4 0 1] [3 0 1]]]
              [43 1]
              "autocons"]
             [[[132 19] [10 37 [4 0 3]]]
              20
              "hint operator"]
             [[42 [7 [4 0 1] [4 0 1]]]
              44
              "composed (7) increment"]
             [[42 [8 [4 0 1] [0 1]]]
              [43 42]
              "op 8"]
             [[42 [6 [1 0] [4 0 1] [1 233]]]
              43
              "conditional yes"]
             [[42 [6 [1 1] [4 0 1] [1 233]]]
              233
              "conditional no"]
             [[42 [8 [1 0] 8 [1 6 [5 [0 7] 4 0 6] [0 6] 9 2 [0 2] [4 0 6] 0 7] 9 2 0 1]]
              41
              "decrement"]
             ]]
            (is (= (nock (noun sub) (noun fom)) res) msg))))
