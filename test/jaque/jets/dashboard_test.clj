(ns jaque.jets.dashboard-test
  (:refer-clojure :exclude [atom zero? dec])
  (:require [jaque.jets.dashboard :refer :all]
            [jaque.noun.box :refer :all]
            [jaque.noun.read :refer :all]
            [jaque.noun.nlr :refer [by-put]]
            [jaque.noun.motes :refer [defmote]]
            [jaque.noun.math :as math]
            [jaque.jets.jet :refer [defjet]]
            [jaque.constants :refer :all]
            [clojure.test :refer :all])
  (:import jaque.noun.Atom))

(deftest skip-hints-test
  (is (= (noun [1 10 9 0 1]) (skip-hints (noun [10 1 10 [2 1 1] 10 3 1 10 9 0 1])))))

(deftest hook-axis-test
  (is (= (atom 14) (hook-axis (noun [9 14 0 1]))))
  (is (= (atom 7)  (hook-axis (noun [0 7]))))
  (is (= (atom 2)  (hook-axis (noun [10 1 9 2 0 1])))))

(deftest hot-names-test
  (is (= (noun {2  :add,
                14 :sub, 
                4  :barfbag})
         (hot-names (noun {:add     [9 2 0 1],
                           :sub     [0 14],
                           :wrong   [10 11 9 16 0 6],
                           :barfbag [10 12 9 4 0 1]})))))

(deftest jet-sham-test
  (is (= (atom 0xb24903ca4f712e271f8c3a5d0402cbaa)
         (jet-sham (noun [:add 7 no 151])))))

(deftest clue-list->map-test
  (is (= a0 (clue-list->map a0)))
  (is (= nil (clue-list->map (noun [[[0 0] [0 12]] 0]))))
  (is (= (noun {:add [9 2 0 1],
                :sub [0 14]})
         (clue-list->map (noun (list [:add 9 2 0 1]
                                     [:sub 0 14]))))))

(deftest clue-parent-axis-test
  (is (= a0  (clue-parent-axis (noun [1 0]))))
  (is (= nil (clue-parent-axis (noun [9 2 0 1]))))
  (is (= a7  (clue-parent-axis (noun [0 7]))))
  (is (= (atom 13) (clue-parent-axis (noun [0 13])))))

(defmote fast)

(deftest chum-test
  (is (= a7 (chum a7)))
  (is (= %fast (chum %fast)))
  (is (= (string->cord "fast13") (chum (noun [%fast 13]))))
  (is (= nil (chum (noun [%fast 80835984791392327435748572173984745723874247])))))

(deftest fsck-test
  (is (= nil (fsck (noun [%fast 3 0]))))
  (is (= [%fast a3 a0] (fsck (noun [%fast [0 3] 0]))))
  (is (= [%fast a3 (noun {:foo [9 4 0 1],
                          :bar [9 5 0 1]})]
         (fsck (noun [%fast [0 3] (list [:foo [9 4 0 1]]
                                        [:bar [9 5 0 1]])])))))


(defn calc [m]
  (let [batt (head (:core m))
        cope (:cope m)
        bash (jet-sham cope)
        club (:club m)]
    (merge m {:batt batt
              :bash bash
              :clog (noun [cope {batt club}])
              :calx (noun [(:calf m) [bash cope] club])})))
(def kernel
  (let [core (noun [[1 151] 151])
        corp (cell yes core)
        clue (noun [:k151 [1 0] (list [:vers 9 2 0 1])])]
    (calc {:core core
           :cope (noun [:k151 3 no 151])
           :club (noun [corp {:vers [9 2 0 1]}])
           :calf (noun [0 {2 :vers} (list :k151) 0])
           :corp corp
           :clue clue
           :fsck (fsck clue)})))

(def dec
  (let [core (noun [[6 [5 [0 7] 4 0 6] [0 6] 9 2 [0 2] [4 0 6] 0 7] 0 (:core kernel)])
        corp (cell no (:batt kernel))
        clue (noun [:dec [0 7] 0])]
    (calc {:core core
           :cope (noun [:dec 7 yes (:bash kernel)])
           :club (noun [corp 0])
           :calf (noun [0 0 (list :dec :k151) 0])
           :corp corp
           :clue clue
           :fsck (fsck clue)})))

(def ver
  (let [core (noun [[9 2 0 3] (:core kernel)])
        corp (cell yes core)
        clue (noun [:ver [0 3] 0])]
    (calc {:core core
           :cope (noun [:ver 3 yes (:bash kernel)])
           :club (noun [corp 0])
           :calf (noun [0 0 (list :ver :k151) 0])
           :corp corp
           :clue clue
           :fsck (fsck clue)})))

(defn mined [old reg]
  {:warm (assoc (:warm old) (:batt reg) (:calx reg))
   :cold (by-put (:cold old) (:bash reg) (:clog reg))})

(def fake   {:warm {}, :cold a0})
(def mine-a (mine fake (:core kernel) (:fsck kernel)))
(def mine-b (mine mine-a (:core dec) (:fsck dec)))
(def mine-c (mine mine-b (:core ver) (:fsck ver)))

(deftest mine-test
  (is (= mine-a (mined fake kernel)))
  (is (= mine-b (mined mine-a dec)))
  (is (= mine-c (mined mine-b ver))))

(deftest fine-test
  (is (= true  (fine? mine-c (:corp dec) (:cope dec) (:core dec))))
  (is (= false (fine? mine-c (:corp dec) (:cope dec) (noun [(:batt dec) 5]))))
  (is (= true  (fine? mine-c (:corp ver) (:cope ver) (:core ver)))))

(defjet overview-dec [dec k151] - [+>] math/sub)

(deftest overview-test
  (let [d (-> empty-dashboard
            (.declare (:core kernel) (:clue kernel))
            (.declare (:core dec) (:clue dec))
            (.install overview-dec))
        h (.hook d (:core dec) "vers")]
    (is (= [(:core kernel) (noun [9 2 0 1])] [(.core h) (.formula h)]))
    (is (= overview-dec (.find d (:core dec) a2)))))
