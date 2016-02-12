(ns cljnock.bit
  (:refer-clojure :exclude [cat]))

(defn bex [n] (bit-shift-left 1 n))

(defn lsh [bloq n x]
  (bit-shift-left x (* n (bex bloq))))

(defn rsh [bloq n x]
  (unsigned-bit-shift-right x (* n (bex bloq))))

(def mix bit-xor)

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
