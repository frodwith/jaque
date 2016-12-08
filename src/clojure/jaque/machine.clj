(ns jaque.machine
  (:require [jaque.noun.read :refer [if& if| head tail lark]]))

(defn mine [core cey]
  ;; left off here
  )

(defn fine? [machine corp cope core]
  (loop [cup corp
         mop cope
         cor core]
  (if& (head cup)
    (= cor (tail cup))
  (let [par (lark +> mop)
        pax (lark +< mop)]
  (if| (head par)
    (do (assert (= a3 pax))
        (= (tail par) (tail cor)))
  (let [pac (fragment pax cor)]
  (if (or (nil? pac) (not (cell? pac)))
    false
  (let [cax ((:warm machine) (head pac))]
  (if (nil? cax)
    false
  (recur (lark +>- cax) 
         (lark +<+ cax)
         pac))))))))))

(defprotocol Machine
  (install-jet ^Machine [m label axis-or-name jet])
  (register-core ^Machine [m core clue])
  (find-jet [m core axis])
  (hook [m core name-as-string]))

(defrecord JaqueMachine [hot-axis hot-name warm cold]

  Machine

  (install-jet [m label k jet]
    (let [is-axis   (atom? k)
          which-map (if is-axis :hot-axis :hot-name)
          map-key   [label (if is-axis k (string->cord k))]]
      (assoc-in m [which-map map-key] jet)))

  (register-core [m core clue]
    (let [bat (head core)
          cax (get warm bat)]
    (if (nil? cax)
      m
    (let [cey (fsck clue)]
    (if (nil? cey)
      m
    (mine m core cey))))))

  (find-jet [m core axis]
    (let [bat (head core)
          cax (get warm bat)]
    (if (nil? cax)
      nil
    (if-not (fine? m (lark +>- cax)
                     (lark +<+ cax)
                     core)
      nil
    (let [label   (lark ->+< cax)
          by-axis (get hot-axis [label axis])]
    (if-not (nil? by-axis)
      by-axis
    (let [hap   (lark ->- cax)
          uname (get-by hap axis)]
    (if (zero? uname)
      nil
    (get hot-name [label (tail uname)])))))))))

  (hook [m core s]
    (loop [cor core]
    (let [bat (head cor)
          cax (get warm bat)]
    (if (nil? cax)
      nil
    (let [unock (get-by (lark +>+ cax) (string->cord s))]
    (if (zero? unock)
      (recur (fragment (lark +<+>- cax) cor))
    (tail unock)))))))
