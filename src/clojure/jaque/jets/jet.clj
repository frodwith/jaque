(ns jaque.jets.jet
  (:refer-clojure :exclude [zero?])
  (:require [clojure.string :refer [split]]
            [jaque.noun.box :refer [seq->it string->cord]]
            [jaque.noun.read :refer [zero? lark->axis mean]]))

(defprotocol Jet
  (hot-key [jet])
  (apply-core [jet machine core]))

(defrecord JetRec [hot-key arg-axes fun]
  Jet
  (hot-key [j] hot-key)
  (apply-core [jet machine core]
    (apply (partial fun machine)
           (apply (partial mean core) arg-axes))))

(defn ignore-machine [f]
  (fn [m & args] [m (apply f args)]))

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
