(ns jaque.jets.v2-test
  (:refer-clojure :exclude [atom dec cat inc])
  (:require [jaque.jets.v2 :refer :all]
            [jaque.constants :refer :all]
            [jaque.noun.box :refer :all]
            [jaque.noun.motes :refer [defmote]]
            [clojure.test :refer :all]))

(deftest bex-test
  (is (= (bex a0) a1))
  (is (= (bex a1) a2))
  (is (= (bex a2) (atom 4)))
  (is (= (bex a3) (atom 8)))
  (is (= (bex (atom 42)) (atom 4398046511104))))

(deftest lth-test
  (is (= yes (lth a0 a1)))
  (is (= no  (lth a1 a1)))
  (is (= no  (lth a1 a0)))
  (is (= no  (lth a10 a0)))
  (is (= yes (lth a3 a10))))

(deftest mug-test
  (is (= (atom 1097343833) (mug (noun [0 0]))))
  (is (= (atom 669157133)  (mug (noun [0 0 0]))))
  (is (= (atom 763078933)  (mug (noun [[69 42] 31337]))))
  (is (= (atom 18652612)   (mug a0))))

(deftest dor-test
  (is (= yes (dor (noun 1)       (noun [0 0]))))
  (is (= yes (dor (noun [0 1])   (noun [1 0]))))
  (is (= no  (dor (noun 84)      (noun 82))))
  (is (= yes (dor (noun 42)      (noun [42 42]))))
  (is (= no  (dor (noun [42 43]) (noun [42 42])))))

(deftest gor-test
  (is (= yes (gor (noun 1)       (noun [0 0]))))
  (is (= no  (gor (noun [0 1])   (noun [1 0]))))
  (is (= yes (gor (noun 84)      (noun 82))))
  (is (= yes (gor (noun 42)      (noun [42 42]))))
  (is (= yes (gor (noun [42 43]) (noun [42 42])))))

(deftest vor-test
  (is (= yes (vor a0 a1)))
  (is (= no  (vor a1 a0)))
  (is (= no  (vor a3 a10)))
  (is (= yes (vor a10 a2)))
  (is (= no  (vor a3 a0))))

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

(deftest cap-test
  (is (= a2 (cap (atom 4))))
  (is (= a3 (cap (atom 6))))
  (is (= a2 (cap (atom 16))))
  (is (= a3 (cap (atom 15)))))

(deftest con-test
  (is (= (atom 30)             (con (atom 20)            (atom 30))))
  (is (= (atom 103)            (con (atom 99)            (atom 100))))
  (is (= (atom 111)            (con (atom 42)            (atom 69))))
  (is (= (atom 13191442432894) (con (atom 4291984392492) (atom 9321673753214)))))

(deftest lsh-test
  (is (= (atom 12)       (lsh a0 a1 (atom 6))))
  (is (= (atom 512)      (lsh a2 a2 a2)))
  (is (= (atom 160)      (lsh a1 a2 a10)))
  (is (= (atom 50331648) (lsh a3 a3 a3))))

(deftest mas-test
  (is (= a2       (mas (atom 4))))
  (is (= a2       (mas (atom 6))))
  (is (= (atom 8) (mas (atom 16))))
  (is (= (atom 7) (mas (atom 15)))))

(deftest sub-test
  (is (= a1 (sub a3 a2)))
  (is (= a1 (sub a2 a1)))
  (is (= a3 (sub (atom 10) (atom 7))))
  (is (= (atom 27) (sub (atom 69) (atom 42)))))

(deftest met-test
  (is (= a1 (met a0 a1)))
  (is (= a7 (met a2 (atom 123456789))))
  (is (= (atom 39) (met a1 (atom 100000000000000000000000))))
  (is (= a3 (met a5 (atom 100000000000000000000000)))))

(deftest cat-test
  (is (= a1              (cat a3 a1 a0)))
  (is (= a3              (cat a0 a1 a1)))
  (is (= a6              (cat a0 a2 a1)))
  (is (= (atom 17)       (cat a2 a1 a1)))
  (is (= (atom 16711936) (cat a3 (atom 256) (atom 255)))))

(deftest cut-test
  (is (= a1          (cut a0 a1 a1 a2)))
  (is (= a1          (cut a0 a2 a1 a4)))
  (is (= a5          (cut a0 a0 a3 (atom 0xf0d))))
  (is (= (atom 13)   (cut a0 a0 a6 (atom 0xf0d))))
  (is (= (atom 0x30) (cut a0 a4 a6 (atom 0xf0d))))
  (is (= (atom 0x21) (cut a0 a3 a6 (atom 0xf0d)))))

(deftest end-test
  (is (= a4           (end a0 a3 (atom 12))))
  (is (= (atom 12)    (end a1 a3 (atom 12))))
  (is (= (atom 62395) (end a0 (atom 16) (atom 65692431291)))))

(deftest rsh-test
  (is (= (atom 36)     (rsh a1 a1 (atom 145))))
  (is (= a9            (rsh a2 a1 (atom 145))))
  (is (= a5            (rsh a0 a1 a10)))
  (is (= (atom 860002) (rsh a0 (atom 18) (atom 225444443422)))))

(deftest add-test
  (is (= a4 (add a2 a2)))
  (is (= a7 (add a4 a3)))
  (is (= (atom 111) (add (atom 69) (atom 42))))
  (is (= (atom 225444443422) (add (atom 123456789101) (atom 101987654321)))))

(deftest inc-test
  (is (= a1  (inc a0)))
  (is (= a2  (inc a1)))
  (is (= a10 (inc a9)))
  (is (= (atom 225444443423) (inc (atom 225444443422)))))

(deftest dec-test
  (is (= a0  (dec a1)))
  (is (= a1  (dec a2)))
  (is (= a9  (dec a10)))
  (is (= (atom 225444443422) (dec (atom 225444443423)))))

(deftest mix-test
  (is (= a1                 (mix a2 a3)))
  (is (= a0                 (mix a2 a2)))
  (is (= (atom 3073)        (mix (atom 3947) (atom 874))))
  (is (= (atom 65692368896) (mix (atom 62395) (atom 65692431291)))))

(deftest gth-test
  (is (= no  (gth a0 a1)))
  (is (= no  (gth a1 a1)))
  (is (= yes (gth a1 a0)))
  (is (= yes (gth a10 a0)))
  (is (= no  (gth a3 a10))))

(deftest jam-test
  (is (= (atom 817)              (jam (noun [1 1]))))
  (is (= (atom 4657)            (jam (noun [1 2]))))
  (is (= (atom 39689)           (jam (noun [0 19]))))
  (is (= (atom 880218685981125) (jam (noun [[1 [13 14]] [13 14] 1])))))

(deftest mat-test
  (is (= (noun [20 699024]) (mat (atom 0xaaa))))
  (is (= (noun [3 6])       (mat a1)))
  (is (= (noun [6 36])      (mat a2))))

(deftest rub-test
  (is (= (rub a1 (jam (atom 0xaaa))))))

(deftest cue-test
  (is (= (noun [1 1])                   (cue (atom 817))))
  (is (= (noun [1 2])                   (cue (atom 4657))))
  (is (= (noun [0 19])                  (cue (atom 39689))))
  (is (= (noun [[1 [13 14]] [13 14] 1]) (cue (atom 880218685981125)))))

(defmote absolutely positively mosdefinitely)

(deftest shay-test
  (is (= (atom 0x34784d2a173f1d57aee0d8f9d349c92b9dacce66e5e1d93fba306dce21dc700)
         (shay (atom 64) %absolutely)))
  (is (= (atom 0xaada53c39c48c04c7b6b4cc27a5cc21b894f8a820daab7e10dcd1558633d5260)
         (shay (atom 64) %positively)))
  (is (= (atom 0xe0a36d02a0065337880aca49413b441a646bd6fe6ba9cbdc7ba07933d33af2de)
         (shay (atom 64) %mosdefinitely))))

(deftest shax-test
  (is (= (atom 0xc96f25b5951414e1bd7fec172e305a92e9d9c72aca2065409686750691e96eaf)
         (shax %absolutely)))
  (is (= (atom 0xc358e7366e311d8d900f27554da3a2ddf524c896b323c9b3631312ea327fe89b)
         (shax %positively)))
  (is (= (atom 0x7aa858b82d3de72809d901565c89aab51649bd1b855d258719248761f15121e0)
         (shax %mosdefinitely))))

(deftest shas-test
  (is (= (atom 0x1ce2bf10bf5a02cbfcbde0135f5407e49677a4f42efe5983f6bf57d0270de6c0)
         (shas (atom 42) %absolutely)))
  (is (= (atom 0x37f2351e0d9e0d2529a591c117cda3f47a81552ad40a2253d7fce37e79c07371)
         (shas (atom 42) %positively)))
  (is (= (atom 0xebb7f5bcf91b1b05987d437d5d2ee2a58c8c7c914a1373e565652745ad5a6af4)
         (shas (atom 42) %mosdefinitely))))

(deftest shaf-test
  (is (= (atom 0x8a951be491a45b480a02b7c37859e124)
         (shaf (atom 42) %absolutely)))
  (is (= (atom 0x4d736034d9942f76fe5972bf6e0dd085)
         (shaf (atom 42) %positively)))
  (is (= (atom 0x673b892db30868e0fd186438f0748851)
         (shaf (atom 42) %mosdefinitely))))

(deftest sham-test
  (is (= (atom 0x441cc79ec97695a1bd0f8197cc0447ef)
         (sham %absolutely)))
  (is (= (atom 0xa6f40dbba7f81a6305a9786e61b53d4f)
         (sham %positively)))
  (is (= (atom 0x18ae1771143f27348c56c104de76d128)
         (sham %mosdefinitely))))
