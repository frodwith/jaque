(ns jaque.interpreter
  (:refer-clojure :exclude [zero? atom])
  (:require [jaque.constants :refer :all]
            [jaque.noun.read :refer :all]
            [jaque.noun.box :refer :all]
            [jaque.jets.dashboard :refer [empty-dashboard]]
            [jaque.error :as e])
  (:import (jaque.noun Atom Noun)
           (jaque.interpreter Dashboard Hint Interpreter Bail Machine Result)))

(defrecord MachineRec [dash]
  Machine
  (^Result startHint [^Machine m ^Hint h]
    (Result. m nil))
  (^Machine endHint [^Machine m ^Hint h ^Noun product]
    (case (cord->string (.kind h))
      "fast" (assoc m :dash (.declare ^Dashboard dash product (.clue h)))
      m))
  (^Result escape [^Machine m ^Noun gat ^Noun sam] (prn sam) (e/exit))
  (^Dashboard dashboard [^Machine m] dash))

(def empty-machine (->MachineRec empty-dashboard))

(defn nock [machine subject formula]
  (try
    (let [x (Interpreter/nock machine subject formula)]
      [(.m x) (.r x)])
    (catch Bail _
      (e/exit))))

;;(defn nock [machine subject formula]
;;  (let [operator  (head formula)
;;        arguments (tail formula)]
;;    (if (cell? operator)
;;      (let [[m1 head-product] (nock machine subject operator)
;;            [m2 tail-product] (nock m1 subject arguments)]
;;        [m2 (cell head-product tail-product)])
;;      (case (.intValue ^Atom operator)
;;        0  (let [part (fragment arguments subject)]
;;           (if (nil? part)
;;             (e/exit)
;;           [machine part]))
;;        1  [machine arguments]
;;        2  (let [[m1 new-subject] (nock machine subject (head arguments))
;;                 [m2 new-formula] (nock m1 subject (tail arguments))]
;;             (nock m2 new-subject new-formula))
;;        3  (let [[m1 x] (nock machine subject arguments)]
;;             [m1 (if (cell? x) yes no)])
;;        4  (let [[m1 x] (nock machine subject arguments)]
;;             [m1 (.add x a1)])
;;        5  (let [[m1 x] (nock machine subject arguments)]
;;             [m1 (if (= (head x) (tail x)) yes no)])
;;        6  (let [[m1 t] (nock machine subject (head arguments))]
;;             (nock m1 subject ((if& t head tail) (tail arguments))))
;;        7  (let [[m1 x] (nock machine subject (head arguments))]
;;             (nock m1 x (tail arguments)))
;;        8  (let [[m1 x] (nock machine subject (head arguments))]
;;             (nock m1 (cell x subject) (tail arguments)))
;;        9  (let [[m1 core] (nock machine subject (tail arguments))
;;                 axis      (head arguments)
;;                 jet       (.find-jet (.dashboard m1) core axis)]
;;             (if (nil? jet)
;;               (nock m1 core (fragment axis core))
;;               (.apply-core jet m1 core)))
;;        10 (let [hint-formula (head arguments)
;;                 next-formula (tail arguments)]
;;             (let [hint-cell         (cell? hint-formula)
;;                   kind              (if hint-cell
;;                                       (head hint-formula)
;;                                       hint-formula)
;;                   [m1 clue]         (if hint-cell
;;                                       (nock machine subject (tail hint-formula))
;;                                       [machine 0])
;;                   [m2 hint-product] (.start-hint m1 kind clue subject next-formula)]
;;               (if-not (nil? hint-product)
;;                 [m2 hint-product]
;;                 (let [[m3 product] (nock m2 subject next-formula)]
;;                   [(.end-hint m3 kind clue subject next-formula product) product]))))
;;        11 (let [[m1 x] (nock machine subject arguments)]
;;             [m1 (.escape m1 x)])))))

(defn slam [gate sample]
  (nock (cell (head gate) sample (lark +> gate))
        (noun [9 2 0 1])))
