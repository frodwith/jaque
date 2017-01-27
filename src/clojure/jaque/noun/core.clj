(ns jaque.noun.core
  (:refer-clojure :exclude [atom zero?])
  (:require [slingshot.slingshot :refer :all]
            [jaque.constants :refer :all]
            [jaque.error :as e])
  (:import clojure.lang.BigInt
           java.math.BigInteger
           (jaque.noun Atom Cell Noun)))

;; This is an internal namespace that exists primarily to resolve circular
;; dependency issues. All functions are re-exported by other namespaces, which
;; is (usually) where you should refer to them.

(defn export-impl [src-ns dst-ns sym]
  (let [v (ns-resolve src-ns sym)
        m (meta v)
        v (intern dst-ns sym v)]
    (alter-meta! v #(merge % m))
    v))

(defmacro export [& vars]
  (let [one   (fn [n] `(export-impl 'jaque.noun.core *ns* '~n))
        forms (map (comp one symbol name) vars)]
    `(do ~@forms)))

;; Good place for this, really.
(defmethod print-dup jaque.noun.Atom [o w]
  (print-ctor o (fn [o w]
                  (print-dup (if (.isCat o) 
                               (.intValue o)
                               (.toString o))
                             w)) w))

(def atom?
  (partial instance? Atom))

(defn atom ^Atom [a]
  (cond (atom? a)    a
        (integer? a) (cond (instance? BigInteger a)
                             (Atom/fromByteArray (.toByteArray ^BigInteger a) Atom/BIG_ENDIAN)
                           (instance? BigInt a)
                             (let [^BigInt a a]
                               (if (nil? (.bipart a))
                                 (atom (.lpart a))
                                 (atom (.bipart a))))
                           :else (Atom/fromLong a))
        (char? a)    (Atom/fromLong (int a))
        (string? a)  (Atom/fromString a)
        :else        (throw+ {:message "atom must be passed an integer or a string"
                              :bad-atom a})))

(defn zero? [^Noun n]
  (.isZero n))

(def cell? 
  (partial instance? Cell))

(defn cell ^Cell [& xs]
  ((fn $ [c xs]
     (cond (< c 2) (throw+ {:message "A cell must be at least two things."
                            :count    c
                            :bad-cell xs})
           (= c 2) (Cell. ^Noun (first xs) ^Noun (second xs))
           :else   (Cell. ^Noun (first xs) ^Noun ($ (dec c) (rest xs)))))
   (count xs) xs))

(defn head [^Cell c]
  (.p c))

(defn tail [^Cell c]
  (.q c))

(def noun?
  (partial instance? Noun))

(defn bloq [^Atom a]
  (let [v (.intValue a)]
    (if (or (< v 0) (> v 32))
      (e/fail)
      v)))

(defn lsh [^Atom a ^Atom b ^Atom c]
  (when-not (.isCat b) (e/fail))
  (let [a   (bloq a)
        b   (.intValue b)
        len (.met c a)]
    (if (= 0 len)
      a0
      (let [lus (+ b len)]
        (when-not (>= lus len) (e/fail))
        (let [sal (Atom/slaq a lus)]
          (Atom/chop a 0 len b sal c)
          (Atom/malt sal))))))

(defn bex [^Atom a]
  (lsh a0 a a1))

(defn cap [^Atom a]
  (case (.cap a)
    2 a2
    3 a3
    (e/exit)))

(defn mas [^Atom a]
  (let [a (.mas a)]
    (if (nil? a)
      (e/exit)
      a)))

(defn fragment-path [^Atom axis]
  (loop [a axis, p nil]
  (if (= a1 a)
    p
  (let [c (if (= a2 (cap a)) :left :right)]
  (recur (mas a) (cons c p))))))

(defn inline-fragment [axe subject]
  (reduce (fn [s dir]
            (let [tagged (with-meta s {:tag 'jaque.noun.Cell})]
              (case dir
                :left  `(.p ~tagged)
                :right `(.q ~tagged))))
          subject
          (reverse (fragment-path axe))))

(defn ^Atom lark->axis [s]
  (if-not (re-find #"^[+-](?:[<>][+-])*[<>]?$" s)
    a0
    (let [bits (map #(case % \- \0
                             \+ \1
                             \< \0
                             \> \1)
                    (seq s))]
      (Atom/fromString (apply str (conj bits \1)) 2))))

; throws ClassCastException if lark isn't a valid path for n
(defmacro lark [sym n]
  (inline-fragment (lark->axis (name sym)) n))

(defn lth [^Atom a ^Atom b]
  (if (= -1 (.compareTo a b)) yes no))

(defn dor [a b]
  (loop [a a
         b b]
  (if (= a b)
    yes
  (if (atom? a)
    (if (atom? b)
      (lth a b)
    yes)
  (if (atom? b)
    no
  (if (= (head a) (head b))
    (recur (tail a) (tail b))
  (recur (head a) (head b))))))))

(defn mug [^Noun a] 
  (Atom/fromLong (.hashCode a)))

(defn gor [a b]
  (let [c (mug a)
        d (mug b)]
    (if (= c d)
      (dor a b)
      (lth c d))))

(defn vor [a b]
  (let [c (mug (mug a))
        d (mug (mug b))]
    (if (= c d)
      (dor a b)
      (lth c d))))

(defmacro if& [t y n]
  `(let [r# ~t]
     (cond (= yes r#) ~y
           (= no  r#) ~n
           :else (e/exit))))

(declare noun)

(defn by-put [a b c]
  (if (zero? a)
    (noun [[b c] 0 0])
  (if (= b (lark -< a))
    (if (= c (lark -> a))
      a
    (noun [[b c] (lark +< a) (lark +> a)]))
  (if& (gor b (lark -< a))
    (let [d (by-put (lark +< a) b c)]
    (if& (vor (lark -< a) (lark -< d))
      (noun [(head a) d (lark +> a)])
    (noun [(head d) (lark +< d) (head a) (lark +> d) (lark +< a)])))
  (let [d (by-put (lark +> a) b c)]
  (if& (vor (lark -< a) (lark -< d))
    (noun [(head a) (lark +< a) d])
  (noun [(head d) [(head a) (lark +< a) (lark +< d)] (lark +> d)])))))))

(defn map->nlr [m]
  (reduce #(by-put %1 (noun (%2 0)) (noun (%2 1)))
          (atom 0) m))

(defn string->cord ^Atom [^String s]
  (Atom/fromByteArray (.getBytes s "UTF-8") Atom/LITTLE_ENDIAN))

(defn seq->it [s]
  (reduce #(cell (noun %2) (noun %1)) (atom 0) (reverse s)))

(defn noun [v]
  (cond (noun? v)    v
        (integer? v) (atom v)
        (char? v)    (atom (int v))
        (keyword? v) (string->cord (name v))
        (vector? v)  (apply cell (map noun v))
        (map? v)     (map->nlr v)
        (string? v)  (seq->it (seq v))
        (seq? v)     (seq->it v)
        :else        (throw+ {:message "bad argument to noun"
                              :bad-noun v})))

(defn met [a b]
  (let [a (bloq a)
            i (.met b a)]
        (Atom/fromLong i)))
