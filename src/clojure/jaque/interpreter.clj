(ns jaque.interpreter
  (:require [jaque.constants :refer [yes no a1]])
  (:require [jaque.jets :refer [lookup]])
  (:require [jaque.hints :refer [start finish]])
  (:require [jaque.virtual :refer [escape]])
  (:require [jaque.read :refer [fragment head tail cell?]])
  (:import jaque.noun Cell))

(defn nock [machine subject formula]
  (let [operator  (head formula)
        arguments (tail formula)]
    (if (cell? operator)
      (let [[m1 head-product] (nock machine subject operator)
            [m2 tail-product] (nock m1 subject arguments)]
        [m2 (Cell. head-product tail-product)])
      (case operator
        0  [machine (fragment arguments subject)]
        1  [machine arguments]
        2  (let [[m1 new-subject] (nock machine subject (head arguments))
                 [m2 new-formula] (nock m1 subject (tail arguments))]
             (nock m2 new-subject new-formula))
        3  (let [[m1 x] (nock machine subject arguments)]
             [m1 (if (cell? x) yes no)])
        4  (let [[m1 x] (nock machine subject arguments)]
             [m1 (.add x a1)])
        5  (let [[m1 x] (nock machine subject arguments)]
             [m1 (if (= (head x) (tail x)) yes no)])
        6  (let [[m1 t] (nock machine subject (head arguments))]
             (nock m1 subject ((case t yes head no tail) (tail arguments))))
        7  (let [[m1 x] (nock machine subject (head arguments))]
             (nock m1 x (tail arguments)))
        8  (let [[m1 x] (nock machine subject (head arguments))]
             (nock m1 (Cell. x subject) (tail arguments)))
        9  (let [[m1 core] (nock machine subject (tail arguments))
                 axis      (head arguments)
                 jet       (lookup m1 core axis)]
             (if (nil? jet)
               (nock m1 core (fragment axis core))
               (jet core)))
        10 (let [hint-formula (head arguments)
                 next-formula (tail arguments)]
             (let [hint-cell         (cell? hint-formula)
                   kind              (if hint-cell
                                       (head hint-formula)
                                       hint-formula)
                   [m1 clue]         (if hint-cell
                                       (nock machine subject (tail hint-formula))
                                       [machine 0])
                   [m2 hint-product] (start m1 kind clue subject next-formula)]
               (if-not (nil? hint-product)
                 (let [product (nock m2 subject next-formula)]
                   [(end m2 kind clue subject formula product) product])
                 [m2 hint-product])))
        11 (let [[m1 x] (nock machine subject arguments)]
             [m1 (escape m1 x)])))))
