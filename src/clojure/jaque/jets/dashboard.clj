(ns jaque.jets.dashboard
  (:refer-clojure :exclude [atom zero?])
  (:require [jaque.constants :refer :all]
            [jaque.noun.read :refer :all]
            [jaque.noun.box  :refer :all]
            [jaque.noun.bits :refer [end]]
            [jaque.noun.hash :refer [shax]]
            [jaque.noun.pack :refer [jam]]
            [jaque.noun.nlr  :refer [by-put by-get nlr-seq]])
  (:import (jaque.interpreter Dashboard Result Hook)
           (jaque.noun Atom)))

(defn calx-key [bat]
  (System/identityHashCode bat))

(defn battery->calx [d bat]
  (get (:warm d) (calx-key bat)))

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

(defn hot-names [huk]
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
  (if (zero? l)
    m
  (if-not (cell? l) 
    nil
  (let [[i t] [(head l) (tail l)]]
  (if-not (cell? i)
    nil
  (let [term (head i), nock (tail i)]
  (if-not (atom? term)
    nil
  (recur (by-put m term nock) t)))))))))

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
  ^Atom (.intValue t)))))))

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
  [nam axis hook-map])))))))))

(defn mine [d core [core-name parent-axis term->nock]]
  (let [battery (head core)
        [cope corp parent-label]
          (if (zero? parent-axis)
            [(cell core-name a3 no (tail core))
             (cell yes core)
             a0]
          (let [parent (fragment parent-axis core)]
          (if (or (nil? parent)
                  (not (cell? parent)))
            nil
          (let [parent-battery (head parent)
                parent-calx    (battery->calx d parent-battery)]
          (if (nil? parent-calx)
            nil
          (let [parent-bash (lark +<- parent-calx)
                parent-corp (lark +>- parent-calx)]
            [(cell core-name parent-axis yes parent-bash)
             (if (and (= a3 parent-axis)
                      (= yes (head parent-corp)))
               (cell yes core)
               (cell no  parent-battery))
             (lark ->+< parent-calx)]))))))]
  (if (nil? cope)
    d
  (let [bash       (jet-sham cope)
        club       (cell corp term->nock)
        uold-clog  (by-get (:cold d) bash)
        bat->club  (by-put (if (zero? uold-clog)
                             a0
                             (lark +> uold-clog))
                           battery
                           club)
        new-clog   (cell cope bat->club)
        d          (assoc d :cold (by-put (:cold d) bash new-clog))
        label      (cell core-name parent-label)
        axis->name (hot-names term->nock)
        calf       (cell a0 axis->name label a0)
        calx       (cell calf (cell bash cope) club)]
    (println "new jet: " (clojure.string/join "/" (reverse (map cord->string (seq label)))))
  (assoc-in d [:warm (calx-key battery)] calx)))))

(defn fine? [d corp cope core]
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
  (let [cax (battery->calx d (head pac))]
  (if (nil? cax)
    false
  (recur (lark +>- cax) 
         (lark +<+ cax)
         pac))))))))))

(defn fine-help [d core]
  (let [bat (head core)
        cax (battery->calx d bat)
        fin (and (not (nil? cax))
                 (fine? d (lark +>- cax) 
                          (lark +<+ cax)
                          core))]
    [cax fin]))

(defrecord DashRec [hot warm cold]

  Dashboard
  (install [d jet]
    (assoc-in d [:hot (:hot-key jet)] jet))

  (declare [d core clue]
    (let [bat (head core)
          cax (battery->calx d bat)]
    (if-not (nil? cax)
      d
    (let [cey (fsck clue)]
    (if (nil? cey)
      d
    (mine d core cey))))))

  (fine [d core]
    (let [[cax fin] (fine-help d core)]
      fin))

  ;; Jet or nil
  (find [d core axis]
    (let [[cax fin] (fine-help d core)]
    (if-not fin nil
    (let [label   (lark ->+< cax)
          by-axis (hot [label :axis axis])]
    (if-not (nil? by-axis)
      by-axis
    (let [axis->name (lark ->- cax)
          uname      (by-get axis->name axis)]
    (if (zero? uname)
      nil
    (hot [label :name (tail uname)]))))))))

  ;; [core nock-formula] or nil
  (hook [d core s]
    (loop [cor core]
    (let [bat (head cor)
          cax (battery->calx d bat)]
    (if (nil? cax)
      nil
    (let [unock (by-get (lark +>+ cax) (string->cord s))]
    (if (zero? unock)
      (recur (fragment (lark +<+>- cax) cor))
    (Hook. cor (tail unock)))))))))

(def empty-dashboard (map->DashRec {:hot  {}
                                    :warm {}
                                    :cold a0}))
