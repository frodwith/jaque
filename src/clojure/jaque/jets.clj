(ns jaque.jets
  (:require [jaque.noun]
            [jaque.error :as e]
            [clojure.string :refer [ends-with?]])
  (:import (jaque.noun Atom)))

(defn untar [s]
  (let [n (name s)
        c (ends-with? n "*")
        n (if c (subs n 0 (dec (count n))) n)
        s (if c (with-meta (symbol n) (meta s)) s)]
    [s c]))

(defn tag [cel]
  (if cel 'jaque.noun.Cell 'jaque.noun.Atom))

(defn add-type [s]
  (let [[s cel] (untar s)]
    (vary-meta s assoc :tag (tag cel))))

(defmacro defj [nam args & body]
  (let [[s cel]  (untar nam)
        args (mapv add-type args)
        args (vary-meta args assoc :tag (tag cel))]
    `(defn ~s ~args ~@body)))

(defn bloq [^Atom a]
  (let [v (.intValue a)]
    (if (or (< v 0) (> v 32))
      (e/fail)
      v)))
