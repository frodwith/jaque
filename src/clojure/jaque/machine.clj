(ns jaque.machine
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.constants :refer :all]
            [jaque.noun.read :refer :all]
            [jaque.noun.box  :refer :all]
            [jaque.jets.v2 :refer [end shax jam by-put by-get]])
  (:import (jaque.noun Atom)))

(defprotocol Machine
  (install-jet [m label axis-or-name jet])
  (register-core [m core clue])
  (find-jet [m core axis])
  (hook [m core name-as-string]))

(defn skip-hints [formula]
  (loop [f formula]
  (if-not (and (cell? f) (= a10 (head f)))
    f
  (recur (lark +> f)))))

;; axis (a0 for none)
(defn hook-axis [formula]
  (let [formula   (skip-hints formula)
        [p q r]   (trel-seq formula)]
  (if (nil? p)
    (if (or (not (cell? formula))
            (not (zero? (head formula)))
            (cell? (tail formula))
            (not (.isCat ^Atom (tail formula))))
      a0
    (tail formula))
  (if (or (not= a9 p)
          (cell? q)
          (not (.isCat ^Atom q))
          (atom? r)
          (not (zero? (head r)))
          (not= a1 (tail r)))
    a0
  q))))

(defn hot-names [m huk]
  (reduce (fn [m kv]
          (let [term (head kv)
                axis (hook-axis (tail kv))]
          (if (zero? axis)
            m
          (by-put m axis term))))
          a0 (nlr-seq huk)))

(defn jet-sham [a]
  (end a7 a1 (shax (jam a))))

(defn clue-list->map [clue-list]
  (loop [m a0, l clue-list]
  (if-not (cell? l) 
    nil
  (let [[i t] [(head l) (tail l)]]
  (if-not (cell? i)
    nil
  (let [[axis term] [(head i) (tail i)]]
  (if-not (atom? axis)
    nil
  (recur (by-put m axis term) t))))))))

(defn clue-parent-axis [formula]
  (let [f (skip-hints formula)]
  (if-not (cell? f)
    nil
  (let [a (tail f)]
  (cond (= (noun [1 0]) f)
    a0
  (and (= a0 (head f))
       (atom? a)
       (.isCat ^Atom a))
    a
  :else
    nil)))))

(defn chum [c]
  (if (atom? c)
    c
  (let [h (head c), t (tail c)]
  (if (or (cell? t)
          (not (.isCat ^Atom t)))
    nil
  (string->cord 
  (format "%s%d" 
    (cord->string h) 
  ^Atom t))))))

(defn fsck [clue]
  (let [[p q r] (trel-seq clue)]
  (if (nil? p)
    nil
  (let [nam (chum p)]
  (if (nil? nam)
    nil
  (let [axis (clue-parent-axis q)]
  (if (nil? axis)
    nil
  (let [hook-map (clue-list->map r)]
  (if (nil? hook-map)
    nil
  (cell nam axis hook-map))))))))))

(defn mine [m core cey]
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
       (let [parent-bash (lark +<- parent-calx)
             parent-corp (lark +>- parent-calx)]
         [(cell core-name parent-axis yes parent-bash)
          (if (and (= a3 parent-axis)
                   (= yes (head parent-corp)))
            (cell yes core)
            (cell no  parent-battery))
          (lark ->+< parent-calx)]))))))
     bash       (jet-sham cope)
     club       (cell corp term->nock)
     uold-clog  (by-get (:cold m) bash)
     bat->club  (by-put (if (zero? uold-clog)
                          a0
                          (lark +> uold-clog))
                        battery
                        club)
     new-clog   (cell cope bat->club)
     m          (assoc m :cold (by-put (:cold m) bash new-clog))
     label      (cell core-name parent-label)
     axis->name (hot-names m term->nock)
     calf       (cell a0 axis->name label a0)
     calx       (cell calf (cell bash cope) club)]
  (assoc-in m [:warm battery] calx)))

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
    (if-not (nil? cax)
      m
    (let [cey (fsck clue)]
    (if (nil? cey)
      m
    (mine m core cey))))))

  ;; Jet or nil
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
          uname      (by-get axis->name axis)]
    (if (zero? uname)
      nil
    (get hot-name [label (tail uname)])))))))))

  ;; [core nock-formula] or nil
  (hook [m core s]
    (loop [cor core]
    (let [bat (head cor)
          cax (get warm bat)]
    (if (nil? cax)
      nil
    (let [unock (by-get (lark +>+ cax) (string->cord s))]
    (if (zero? unock)
      (recur (fragment (lark +<+>- cax) cor))
    [cor (tail unock)])))))))
