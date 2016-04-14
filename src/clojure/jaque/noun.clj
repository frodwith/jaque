(ns jaque.noun
  (:import jaque.noun.Atom))

(deftype Cell [p q]
  Object
  (equals [a b] (and (instance? Cell b)
                     (let [^Cell b b]
                       (and (= (.p a) (.p b))
                            (= (.q a) (.q b))))))
  (hashCode [c] 
    (+ (* 37 (+ 37 (.hashCode (.p c))))
       (.hashCode (.q c))))

  (toString [c] (format "[%s %s]" (.p c) (.q c))))

(def atom? (partial instance? Atom))
(def cell? (partial instance? Cell))
(defn noun? [a] (or (atom? a) (cell? a)))

(defn noun [a]
  (if (noun? a)
    a
    (if (number? a)
      (Atom/fromLong (long a))
      (let [s (seq a)]
        (if s
          ((fn tr [a] 
             (if (seq a)
               (let [f (noun (first a))
                     r (rest a)]
                 (if (seq r)
                   (->Cell f (tr r))
                   f)))) s)
          (throw (Exception. (format "Cannot make a noun from %s" a))))))))
