(ns jaque.noun.read
  (:refer-clojure :exclude [zero?])
  (:require [jaque.noun.core :refer [mas cap export]]
            [jaque.error     :as e]
            [jaque.constants :refer :all])
  (:import (jaque.noun Atom Noun)))

(export atom? cell? noun? zero? head tail inline-fragment lark->axis lark if&)

(defn cord->string ^String [^Atom c]
  (String. (.toByteArray c Atom/LITTLE_ENDIAN) "UTF-8"))

(defn fragment [^Atom axis ^Noun subject]
  (loop [a axis
         n subject]
  (if (= a1 a)
    n
  (if-not (cell? n)
    nil
  (recur (mas a)
         ((if (= a2 (cap a)) head tail) n))))))

(defn mean [x & as]
  ;; fixme: use minimal traversal instead of fragment
  (map #(fragment % x) as))

; if you want nil instead of an exception when shape is invalid
(defmacro try-lark [sym n]
  `(try (lark ~sym ~n)
     (catch ClassCastException ~'_ nil)))

(defn trel-seq [^Noun n]
  (or
    (and (cell? n)
    (let [p (head n)
          x (tail n)]
    (and (cell? x)
    (let [q (head x)
          r (tail x)]
    [p q r]))))
  nil))

(defn nlr-seq [^Noun n]
  (let [[n l r] (trel-seq n)]
  (if (or (nil? n) (zero? n))
    nil
  (concat (nlr-seq l) (cons n (nlr-seq r))))))

(defmacro if| [t y n]
  `(let [r# ~t]
     (cond (= no  r#) ~y
           (= yes r#) ~n
           :else (e/exit))))
