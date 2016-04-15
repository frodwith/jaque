(ns jaque.jets
  (:require [jaque.noun]
            [clojure.string :refer [ends-with?]])
  (:import [jaque.noun Atom]))

(defn untar [s]
  (let [n (name s)
        c (ends-with? n "*")
        n (if c (subs n (dec (count n))) n)
        s (if c (with-meta (symbol n) (meta s)) s)]
    [s c]))

(defn add-type [s]
  (let [[s cel] (untar s)]
    (vary-meta s assoc :tag (if cel 'jaque.noun.Cell 'jaque.noun.Atom))))

(defmacro defj [nam args & body]
  (let [nam  (add-type nam)
        args (mapv add-type args)]
    `(defn ~nam ~args ~@body)))

(defn bloq [^Atom a]
  (let [v (.intValue a)]
    (assert (>= 32 v))
    v))
