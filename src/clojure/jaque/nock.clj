(ns jaque.nock
  (:refer-clojure :exclude [atom inc])
  (:require [jaque.error :as e]
            [jaque.noun :refer [atom noun? a0 a1 a2]]
            [jaque.jets.math :refer [inc]])

(def yes a0)
(def no  a1)

(defn loob [bool]
  (if bool yes no))

(defn leg [^Cell sub ^Atom axe]
  (loop [dir nil
         axe axe]
    (if (lth axe a2)
      (try
        (reduce (fn [^Cell c dir]
                  (if (= dir 0)
                    (.p c)
                    (.q c))))
        (catch ClassCastException ex (e/fail)))
      (recur (cons (if (.isZero (cut a0 a1 a0 axe)) 1 0)
                   (rsh a0 axe a1))))))

(defn nock [sub fom]
  (let [[hed tal] fom
        pro       (partial nock sub)]
    (if (cell? hed)
      [(pro hed) (pro tal)]
      (case hed
        0   (leg sub tal)
        1   tal
        2   (nock (pro (tal 0)) (pro (tal 1)))
        3   (loob (cell? (pro tal)))
        4   (inc (pro tal))
        5   (let [[p q] (pro tal)]
              (loob (= p q)))
        6   (let [[p q] tal
                  loob  (pro p)]
              (cond (= loob yes) (pro (q 0))
                    (= loob no)  (pro (q 1))
                    :else        (e/fail)))
        7   (nock (pro (tal 0)) (tal 1))
        8   (nock [(pro (tal 0)) sub] (tal 1))
        9   (let [p (pro (tal 1))]
              (nock p (leg p (tal 0))))
        10  (pro (let [[p q] tal]
                   (if (cell? p)
                     (p 1)
                     q)))
        (e/fail)))))
