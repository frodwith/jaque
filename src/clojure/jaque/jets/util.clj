(ns jaque.jets.util
  (:require [jaque.noun.read :refer [lark->axis mean]]))

(defmacro defjet [sym men arg & body]
  `(defn ~sym [a#]
     (let [~arg (apply (partial mean a#) (map lark->axis ~(map name men)))]
       ~@body)))
