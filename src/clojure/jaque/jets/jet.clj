(ns jaque.jets.jet
  (:require [clojure.string :refer [split]]
            [jaque.noun.read :refer [lark->axis mean]]))

; Thanks to https://gist.github.com/odyssomay/1035590 and
; http://grokbase.com/t/gg/clojure/11awnj1azb/extending-ifn
(defmacro defnrecord [ifn & defrecord-body]
  (let [max-arity   20
        args        (repeatedly max-arity gensym)
        make-invoke (fn [n]
                      (let [args (take n args)]
                        `(invoke [_ ~@args] (~ifn ~@args))))]
    `(defrecord
       ~@defrecord-body
       clojure.lang.IFn
       ~@(map make-invoke (range (inc max-arity)))
       (invoke [_ ~@args more#]
         (apply ~ifn (concat (list ~@args) more#)))
       (applyTo [_ args#]
         (apply ~ifn args#)))))

(defprotocol Jet
  (label [j])
  (arm [j])
  (apply-core [j core]))

(defnrecord f JetRec [label axis-or-name mean-seq f]
  Jet
    (label [this] label)
    (arm [this] axis-or-name)
    (apply-core [this core] (apply f (apply (partial mean core) mean-seq))))

(defmacro defjet [sym label arm men arg & body]
  (let [arm-name (name arm)
        arm-axis (lark->axis arm-name)
        arm-id   (if (zero? arm-axis)
                   arm-name
                   arm-axis)]
    `(def ~sym (->JetRec [~@(map name label)]
                         ~arm-id
                         [~@(map lark->axis (map name men))]
                         (fn ~sym ~arg ~@body)))))
