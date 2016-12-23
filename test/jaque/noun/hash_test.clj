(ns jaque.noun.hash-test
  (:refer-clojure :exclude [atom dec cat inc])
  (:require [jaque.constants  :refer :all]
            [jaque.noun.box   :refer :all]
            [jaque.noun.hash  :refer :all]
            [jaque.noun.motes :refer [defmote]]
            [clojure.test     :refer :all]))

(deftest mug-test
  (is (= (atom 1097343833) (mug (noun [0 0]))))
  (is (= (atom 669157133)  (mug (noun [0 0 0]))))
  (is (= (atom 763078933)  (mug (noun [[69 42] 31337]))))
  (is (= (atom 18652612)   (mug a0))))

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
