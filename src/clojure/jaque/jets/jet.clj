(ns jaque.jets.jet
  (:refer-clojure :exclude [zero?])
  (:require [clojure.string :refer [split]]
            [jaque.noun.box :refer [seq->it string->cord]]
            [jaque.noun.read :refer [zero? lark->axis mean]])
  (:import (jaque.interpreter Jet Result)))

(defrecord JetRec [hot-key arg-axes fun]
  Jet
  (argumentLocations [rec]
    (into-array jaque.noun.Atom arg-axes))
  (apply [rec m arguments]
    (clojure.core/apply fun (cons m arguments))))

(defn ignore-machine [f]
  (fn [m & args] (Result. m (apply f args))))

;; define a jet whose implementation is an external function f, whose
;; arguments have already been unpacked (like a u3q function), and who neither
;; recieves a machine as its first argument nor returns a vector of machine
;; and product - it simply returns product.
(defmacro defjet [sym label arm lark f]
  (let [one     (seq->it (map (comp string->cord name) label))
        arm-str (name arm)
        maxis   (lark->axis arm-str)
        axis?   (not (nil? maxis))
        two     (if axis? :axis :name)
        three   (if axis? maxis (string->cord arm-str))
        hot-key [one two three]
        axes    (vec (map (comp lark->axis name) lark))]
  `(def ~sym (map->JetRec {:hot-key  ~hot-key
                           :arg-axes ~axes
                           :fun      ~f}))))
