(ns jaque.error
  (:refer-clojure :exclude [time])
  (:use slingshot.slingshot))

(def bail-types #{:exit :evil :intr :fail :foul :need :meme :time :oops})

(defn bail [t]
  (let [t (if (contains? bail-types t) t :oops)]
    (throw+ {:type ::bail :bail-type t})))

(doseq [t bail-types]
  (intern *ns* (symbol (name t)) #(bail t)))
