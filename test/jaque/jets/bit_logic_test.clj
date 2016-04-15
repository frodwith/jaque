(ns jaque.jets.bit-logic-test
  (:import jaque.noun.Atom)
  (:require [clojure.test :refer :all]
            [jaque.jets.util :refer :all]
            [jaque.jets.bit-logic :as j]))

(def mix (atomize j/mix))

(deftest mix-test
  (testing "mix"
    (is (= (mix 2 3) 1))
    (is (= (mix 2 2) 0))))
