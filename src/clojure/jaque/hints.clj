(ns jaque.hints
  (:require jaque.jets :refer [register])
  (:require jaque.cache :refer [fetch store])
  (:require jaque.marshall :refer [string->cord])
  (:import jaque.noun Cell))

(doseq [mote ["hunk" "lose" "mean" "spot"
              "live" "slog" "germ" "nock"
              "fast" "memo" "sole"]]
  (intern *ns* (symbol (str mote)) (string->cord mote)))

(defn start [machine kind clue subject formula]
  (cond (= kind memo)
          [machine (fetch machine nock (Cell. subject formula))]
        :else
          [machine nil]))

(defn end [machine kind clue subject formula product]
  (cond (= kind fast) (register product clue)
        (= kind memo) (store machine nock (Cell. subject formula) product)
        :else machine))
