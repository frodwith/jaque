(ns jaque.interpreter-test
  (:refer-clojure :exclude [atom])
  (:require [clojure.test :refer :all]
            [slingshot.test :refer :all]
            [jaque.jets.jet :refer [defjet ignore-machine]]
            [jaque.constants :refer :all]
            [jaque.noun.box :refer :all]
            [jaque.noun.math :as math]
            [jaque.error :as e]
            [jaque.interpreter :refer [empty-machine]]
            [jaque.jets.dashboard :refer [empty-dashboard]])
  (:import jaque.truffle.NockLanguage
           jaque.noun.Atom
           jaque.interpreter.Interpreter
           jaque.interpreter.Bail
           jaque.interpreter.Result))

(defjet math-add [add math kmat] - [+<- +<+] (ignore-machine math/add))
(defjet math-sub [sub math kmat] - [+<- +<+] (ignore-machine math/sub))
(defjet math-dec [dec math kmat] - [+<]
  (fn [m a]
    (Result. (assoc m :dec-count (inc (:dec-count m)))
             (math/dec a))))
(defjet math-lth [lth math kmat] - [+<- +<+] (ignore-machine math/lth))

(def math-kernel-formula
  (noun [7 [1 :kmat] 7 [8 [1 1 :kmat] 10 [:fast 1 :kmat [1 0] 0] 0 1] 8 [1 [7 [8 [1 0 0] [1 6 [5 [1 0] 0 12] [0 13] 9 2 [0 2] [[8 [9 47 0 7] 9 2 [0 4] [0 28] 0 11] 4 0 13] 0 7] 0 1] 10 [:fast 1 :add [0 7] 0] 0 1] [7 [8 [1 0 0] [1 6 [6 [5 [0 12] 0 13] [1 1] 1 0] [6 [8 [1 6 [5 [1 0] 0 28] [1 0] 6 [6 [6 [5 [1 0] 0 29] [1 1] 1 0] [6 [9 2 [0 2] [0 6] [[8 [9 47 0 15] 9 2 [0 4] [0 60] 0 11] 8 [9 47 0 15] 9 2 [0 4] [0 61] 0 11] 0 15] [1 0] 1 1] 1 1] [1 0] 1 1] 9 2 0 1] [1 0] 1 1] 1 1] 0 1] 10 [:fast 1 :lth [0 7] 0] 0 1] [7 [8 [1 0] [1 10 [:memo 1 0] 8 [1 6 [8 [9 10 0 15] 9 2 [0 4] [[0 30] 7 [0 3] 1 3] 0 11] [1 1] 8 [8 [9 47 0 15] 9 2 [0 4] [0 30] 0 11] 8 [9 4 0 31] 9 2 [0 4] [[7 [0 3] 9 2 [0 6] [0 14] [0 2] 0 31] 7 [0 3] 9 2 [0 6] [0 14] [8 [9 47 0 31] 9 2 [0 4] [0 6] 0 11] 0 31] 0 11] 9 2 0 1] 0 1] 10 [:fast 1 :fib [0 7] 0] 0 1] [7 [8 [1 0 0] [1 6 [5 [1 0] 0 13] [0 12] 9 2 [0 2] [[8 [9 47 0 7] 9 2 [0 4] [0 28] 0 11] 8 [9 47 0 7] 9 2 [0 4] [0 29] 0 11] 0 7] 0 1] 10 [:fast 1 :sub [0 7] 0] 0 1] 7 [8 [1 0] [1 6 [5 [1 0] 0 6] [0 0] 8 [1 0] 8 [1 6 [5 [0 30] 4 0 6] [0 6] 9 2 [0 2] [4 0 6] 0 7] 9 2 0 1] 0 1] 10 [:fast 1 :dec [0 7] 0] 0 1] 10 [:fast 1 :math [0 3] [:add 9 4 0 1] [:sub 9 46 0 1] [:dec 9 47 0 1] [:fib 9 22 0 1] 0] 0 1]))

(def m0 (assoc empty-machine :dash (-> empty-dashboard
                                       (.install math-add)
                                       (.install math-sub)
                                       (.install math-dec)
                                       (.install math-lth))))

(defn test-nock [nock]
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
              "fragment"]
             [[[132 19] [0 3]]
              19
              "fragment 2"]
             [[57 [4 0 1]]
              58
              "increment"]
             [[[132 19] [4 0 3]]
              20
              "increment2"]
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
      (is (= (nock empty-machine (noun sub) (noun fom)) [empty-machine (noun res)]) msg)))
  
  (testing "bad-fragment"
    (is (thrown+? [:type :jaque.error/bail :bail-type :exit]
                  (nock empty-machine a0 (noun [0 0])))))
  (testing "math-kernel"
    (let [[m1 ken] (nock m0 a0 math-kernel-formula)
          [m2 r]   (nock (assoc m1 :dec-count 0) ken (noun [8 [9 22 0 1] 9 2 [0 4] [1 15] 0 11]))]
      (is (> (:dec-count m2) 0))
      (is (= (atom 610) r)))))

(defn wrap [nock]
  (fn [m s f]
    (try
      (let [^Result r (nock m s f)]
        [(.m r) (.r r)])
    (catch Bail ex
      (e/exit)))))

(deftest u3-inspired
  (test-nock (wrap #(Interpreter/nock %1 %2 %3))))

(deftest truffle
  (test-nock (wrap #(NockLanguage/nock %1 %2 %3))))
