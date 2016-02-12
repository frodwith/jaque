(ns cljnock.wire-test
  (:refer-clojure :exclude [cat])
  (:require [clojure.test :refer :all]
            [cljnock.wire :refer :all]))

(def cued-jammed [[     1    12]
                  [ [1 1]   817]
                  [ [1 2]  4657]
                  [[0 19] 39689]])

(deftest jam-test
  (testing "jam"
    (doseq [[p q] cued-jammed]
      (is (= (jam p) q) p))))

(deftest cue-test
  (testing "cue"
    (doseq [[p q] cued-jammed]
      (is (= (cue q) p) q))))
