(ns cljnock.core)

(def yes   0)
(def no    1)
(def crash "%nash")

(defn noun [v]
  (if (vector? v)
    (let [c (count v)]
      (assert (> c 1))
      (let [p (noun (v 0))
            q (noun (if (= c 2) (v 1) (subvec v 1)))]
        [p q]))
    (do
      (assert (integer? v))
      v)))

(defn cell? [x]
  (and (vector? x) (= (count x) 2)))

(def atom? (comp not vector?))

(defn loob [bool]
  (if bool yes no))

(defn leg [sub axe]
  (loop [dir nil
         axe axe]
    (if (< axe 2)
      (try
        (reduce #(%1 %2) sub dir)
        (catch ClassCastException e crash))
      (recur (cons (if (bit-test axe 0) 1 0) dir)
             (bit-shift-right axe 1)))))

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
                    :else        crash))
        7   (nock (pro (tal 0)) (tal 1))
        8   (nock [(pro (tal 0)) sub] (tal 1))
        9   (let [p (pro (tal 1))]
              (nock p (leg p (tal 0))))
        10  (pro (let [[p q] tal]
                   (if (cell? p)
                     (p 1)
                     q)))
        crash))))
