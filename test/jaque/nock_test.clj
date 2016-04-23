(ns jaque.nock-test
  (:refer-clojure :exclude [atom])
  (:require [clojure.test :refer :all]
            [jaque.noun :refer :all]
            [slingshot.slingshot :refer :all]
            [jaque.nock :refer :all]))

(deftest sugar
  (testing "vector->noun sugar"
    (doseq [[p q] [[[0 1]       [0 1]]
                   [[0 1 2]     [0 [1 2]]]
                   [[0 [1 2 3]] [0 [1 [2 3]]]]
                   [[[0 1 2] [3 4 5] [6 7 8]]
                    [[0 [1 2]] [[3 [4 5]] [6 [7 8]]]]]
                   ]]
      (is (= (noun p) (noun q)) p))))

(deftest axis-test
  (testing "tree-indexing (axis)"
    (let [x (noun [[10 20] [30 40 50 60]])]
      (doseq [[p q] [[0  x]
                     [1  x]
                     [2  (.p x)]
                     [3  (.q x)]
                     [4  10]
                     [5  20]
                     [6  30]
                     [7  [40 50 60]]
                     [8  :exit]
                     [9  :exit]
                     [10 :exit]
                     [11 :exit]
                     [12 :exit]
                     [13 :exit]
                     [14 40]
                     [15 [50 60]]
                     [30 50]
                     [31 60]]]
        (is (= (try+ (axis x (atom p)) (catch [:type :jaque.error/bail ] {:keys [bail-type]} bail-type))
               (if (= q :exit) :exit (noun q))))))))

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
            (is (= ((phi (noun fom)) (noun sub)) (noun res)) msg))))
