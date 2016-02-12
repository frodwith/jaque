(ns cljnock.bit
  (:refer-clojure :exclude [cat]))

(defn bex [n] (.shiftLeft (biginteger 1) n))

(defn lsh [bloq n x]
  (.shiftLeft (biginteger x) (* n (bex bloq))))

(defn rsh [bloq n x]
  (.shiftRight (biginteger x) (* n (bex bloq))))

(defn mix [a b] (.xor (biginteger a) (biginteger b)))

(defn met [bloq blob]
  (loop [blob blob
         c 0]
    (if (= blob 0)
      c
      (recur (rsh bloq 1 blob) (inc c)))))

(defn end [bloq n x]
  (mod x (bex (* n (bex bloq)))))

(defn cat [bloq b c]
  (+ (lsh bloq (met bloq b) c) b))

(defn cut [bloq [b c] d]
  (end bloq c (rsh bloq b d)))
