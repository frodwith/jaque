(ns jaque.jets.dashboard-test
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.jets.dashboard :refer :all]
            [jaque.jets.v2 :refer [by-put]]
            [jaque.noun.box :refer :all]
            [jaque.noun.read :refer :all]
            [jaque.noun.motes :refer [defmote]]
            [jaque.constants :refer :all]
            [clojure.test :refer :all]))

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

;;(def test-kernel (noun
;;  [[[7 
;;      [8 [1 0] [1 [6 [5 [0 7] 4 0 6] [0 6] 9 2 [0 2] [4 0 6] 0 7]] 9 2 0 1]
;;      [10 [:fast 1 :dec [0 7] 0] 0 1]]
;;    [7
;;      [8 [1 0 0] [1 6 [5 [1 0] 0 12] [0 13] 9 2 [0 2] [[8 [9 4 0 7] 9 2 [0 4] [0 28] 0 11] 4 0 13] 0 7] 0 1]
;;      [10 [:fast 1 :add [0 7] 0] 0 1]]]
;;   [1 151]
;;   151]))

(deftest mine-test
  (let [core     (noun [[1 151] 151])
        batt     (head core)
        clue     (fsck (noun [:k151 [1 0] (list [:vers 9 2 0 1])]))
        calf     (noun [0 {2 :vers} (list :k151) 0])
        cope     (noun [:k151 3 no 151])
        bash     (jet-sham cope)
        corp     (cell yes core)
        club     (noun [corp {:vers [9 2 0 1]}])
        clog     (noun [cope {batt club}])
        calx     (noun [calf [bash cope] club])
        fake     {:warm {}, :cold a0}
        mine-a   (mine fake core clue)
        dec-batt (noun [6 [5 [0 7] 4 0 6] [0 6] 9 2 [0 2] [4 0 6] 0 7])
        dec-core (noun [dec-batt 0 core])
        dec-clue (fsck (noun [:dec [0 7] 0]))
        dec-calf (noun [0 0 (list :dec :k151) 0])
        dec-cope (noun [:dec 7 yes bash])
        dec-bash (jet-sham dec-cope)
        dec-corp (cell no batt)
        dec-club (noun [dec-corp 0])
        dec-clog (noun [dec-cope {dec-batt dec-club}])
        dec-calx (noun [dec-calf [dec-bash dec-cope] dec-club])
        mine-b   (mine mine-a dec-core dec-clue)
        ver-batt (noun [9 2 0 3])
        ver-core (noun [ver-batt core])
        ver-clue (fsck (noun [:ver [0 3] 0]))
        ver-calf (noun [0 0 (list :ver :k151) 0])
        ver-cope (noun [:ver 3 yes bash])
        ver-bash (jet-sham ver-cope)
        ver-corp (cell yes ver-core)
        ver-club (noun [ver-corp 0])
        ver-clog (noun [ver-cope {ver-batt ver-club}])
        ver-calx (noun [ver-calf [ver-bash ver-cope] ver-club])
        mine-c   (mine mine-b ver-core ver-clue)]
    (is (= mine-a {:warm {batt calx}, :cold (noun {bash clog})}))
    (is (= mine-b {:warm {batt calx, dec-batt dec-calx},
                   :cold (noun {bash clog, dec-bash dec-clog})}))
    (is (= mine-c {:warm {batt calx, dec-batt dec-calx, ver-batt ver-calx},
                   :cold (noun {bash clog, dec-bash dec-clog, ver-bash ver-clog})}))))
