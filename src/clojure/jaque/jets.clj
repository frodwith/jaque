(ns jaque.jets
  (:require [jaque.constants :refer [a3 yes]])
  (:require [jaque.imprison :refer [cell]])
  (:require [jaque.jets.v2 :refer [get-by]])
  (:require [jaque.read :refer [fragment larf head]]))

(defn fine? [machine cup mop cor]
  (if (= yes (head cup))
    (= cor (tail cup))
    (let [par (larf +> mop)
          pax (larf +< mop)]
      (if (= no (head par))
        (do (assert (= a3 pax))
            (= (tail par) (tail cor)))
        (let [pac (fragment pax cor)]
          (if (or (nil? pac) (not (cell? pac)))
            false
            (let [cax ((:warm machine) (head pac))]
              (if (nil? cax)
                false
                (fine? machine
                       (larf +>- cax)
                       (larf +<+ cax)
                       pac)))))))))

(defn lookup [machine core axis]
  (let [bat (head core)
        cax ((:warm machine) bat)]
    (if (nil? cax)
      nil
      (if-not (fine? machine
                     (larf +>- cax) 
                     (larf +<+ cax)
                     core)
        nil
        ((:hot machine) [(larf ->+< cax) axis])))))

(defn register [machine core clue]
  (let [bat (head core)
    (if ((:warm machine) bat)
      machine
      (let [[leaf-name axis-in-parent hook-map] (check clue)]
        (if (nil? leaf-name)
          machine
          (let [[mop cup lab] 
                (if (= a0 axis-in-parent)
                  [(cell leaf-name 3 no (tail core))
                   (cell no core)
                   0]
                  (let [rah (fragment axis-in-parent core)]
                    (if (or (nil? rah) (not (cell? rah)))
                      nil
                      (let [tab (head rah)
                            cax ((:warm machine) tab)]
                        (if (nil? cax)
                          nil
                          (let [hos (larf +<- cax)
                                puc (larf +>- cax)]
                            [(cell leaf-name axis-in-parent yes hos)
                             (if (and (= a3 axis-in-parent)
                                      (= yes (head puc)))
                               (cell yes core)
                               (cell no tab))
                             (larf ->+< cax)]))))))]
            (if (nil? mop)
              machine
              (let [soh (sham mop)
                    cuz (cell cup hook-map)
                    hoe 
                ;; left off here
