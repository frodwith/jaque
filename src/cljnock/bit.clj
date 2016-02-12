(ns cljnock.bit
  (:refer-clojure :exclude [cat]))

(defn bex [n] (.shiftLeft (biginteger 1) n))

(defn lsh [bloq n x]
  (.shiftLeft (biginteger x) (* n (bex bloq))))

(defn rsh [bloq n x]
  (.shiftRight (biginteger x) (* n (bex bloq))))

(defn mix [a b] (.xor (biginteger a) (biginteger b)))

(defn met [bloq blob]
  (let [a  (biginteger blob)
        ln (.bitLength a)
        sh (unsigned-bit-shift-right ln bloq)
        un (bit-shift-left sh bloq)]
    (if (= un ln)
      sh
      (inc sh))))

(defn end [bloq n x]
  (mod x (bex (* n (bex bloq)))))

(defn cat [bloq b c]
  (+ (lsh bloq (met bloq b) c) b))

(defn cut [bloq [b c] d]
  (end bloq c (rsh bloq b d)))
