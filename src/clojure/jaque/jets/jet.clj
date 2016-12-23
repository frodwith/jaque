(ns jaque.jets.jet
  (:refer-clojure :exclude [zero?])
  (:require [clojure.string :refer [split]]
            [jaque.noun.box :refer [seq->it string->cord]]
            [jaque.noun.read :refer [zero? lark->axis mean]]))

(defprotocol Jet
  (hot-key [jet])
  (apply-core [jet machine core]))

(defrecord JetRec [k m f]
  Jet
  (hot-key [j] k)
  (apply-core [j m c]
    (apply f (apply (partial mean c) m))))

(defn make-hot-key [label-noun axis-or-name]
  (let [maxis (lark->axis (name axis-or-name))
        axis? (not (nil? maxis))
        two   (if axis? :axis :name)
        three (if axis? maxis (string->cord (name axis-or-name)))]
    [label-noun two three]))

(defn build-jet [label-seq arm-sym lark-seq f]
  (let [label (seq->it (map (comp string->cord name) label-seq))
        k     (make-hot-key label (name arm-sym))
        m     (map (comp lark->axis name) lark-seq)]
    (->JetRec k m f)))

(defn ignore-machine [f]
  (fn [m & args] [m (apply f args)]))

;; define a jet whose implementation is an external function f, whose
;; arguments have already been unpacked (like a u3q function), and who neither
;; recieves a machine as its first argument nor returns a vector of machine
;; and product - it simply returns product.
(defmacro defjet [sym label arm lark f]
  `(def ~sym ~(build-jet label arm lark f)))
