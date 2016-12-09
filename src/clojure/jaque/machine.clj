(ns jaque.machine
  (:require [jaque.constants :refer :all]
            [jaque.noun.read :refer [if& if| head tail lark]]
            [jaque.jets.v2 :refer [end shax jam]]))

(defn hot-names [^Machine m huk]
  axis->name)

(defn jet-sham [a]
  (end a7 a1 (shax (jam a))))

(defn ^Machine mine [^Machine m core cey]
  (let
    [battery     (head core)
     core-name   (head cey)
     ey          (tail cey)
     parent-axis (head ey)
     term->nock  (tail ey)
     [cope corp parent-label]
       (if (zero? parent-axis)
         [(cell core-name a3 no (tail core))
          (cell no core)
          a0]
       (let [parent (fragment parent-axis core)]
       (if (or (nil? parent)
               (not (cell? parent)))
         m
       (let [parent-battery (head parent)
             parent-calx    (get (:warm m) parent-battery)]
       (if (nil? parent-calx)
         m
       (let [parent-bash (lark +<- parent-cax)
             parent-corp (lark +>- parent-cax)]
         [(cell core-name parent-axis yes parent-bash)
          (if (and (= a3 parent-axis)
                   (= yes (head parent-corp)))
            (cell yes core)
            (cell no  parent-battery))
          (lark ->+< parent-calx)]))))))
     bash       (jet-sham cope)
     club       (cell corp term->nock)
     uold-clog  (get-by (:cold m) bash)
     bat->club  (put-by (if (zero? uold-clog)
                          a0
                          (lark +> uold-clog))
                        battery
                        club)
     new-clog   (cell cope bat->club)
     m          (assoc m :cold (put-by (:cold m) bash new-clog))
     label      (cell core-name parent-label)
     axis->name (hot-names m term->nock)
     calf       (cell a0 axis->name label a0)
     calx       (cell calf (cell bash cope) club)])
  (assoc-in m [:warm battery] calx))

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
    (let [axis->name (lark ->- cax)
          uname      (get-by axis->name axis)]
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
