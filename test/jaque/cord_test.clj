(ns jaque.cord-test
  (:refer-clojure :exclude [atom])
  (:require [clojure.test :refer :all]
            [jaque.noun :refer [atom]]
            [jaque.cord :refer :all]))

(def examples [[1953718630   "fast"]
               [478560413032 "hello"]
               [1801678702   "nock"]])

(deftest cords
  (testing "basic conversion"
    (doseq [[i s] examples]
      (is (= (cord->string (atom i)) s))
      (is (= (string->cord s) (atom i))))))
