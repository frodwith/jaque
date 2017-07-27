(ns jaque.noun
  (:import clojure.lang.BigInt
           (net.frodwith.jaque.data Atom Cell Noun)))

(defn atom? [a]
  (Noun/isAtom a))

(defn noun? [a]
  (Noun/isNoun a))

(defn cell? [a]
  (Noun/isCell a))

(defn noun [a]
  (cond (noun? a)    a
        (integer? a) (cond (instance? BigInteger a)
                             (Atom/fromByteArray (.toByteArray ^BigInteger a) Atom/BIG_ENDIAN)
                           (instance? BigInt a)
                             (let [^BigInt a a]
                               (if (nil? (.bipart a))
                                 (.lpart a)
                                 (noun (.bipart a))))
                           :else (long a))
        (keyword? a) (Atom/stringToCord (name a))
        (vector? a)  ((fn f [items n]
                        (if (= n 2)
                          (Cell. (noun (first items)) (noun (second items)))
                          (Cell. (noun (first items)) (f (rest items) (dec n)))))
                      (seq a) (count a))
        :else (do (println "bad noun: " a)
                  (throw (IllegalArgumentException.)))))

(defn seq->it [s]
  (noun (conj (into [] s) 0)))

