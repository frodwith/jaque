(ns jaque.fs
  (:use jaque.noun)
  (:require [hawk.core :as hawk]
            [clojure.tools.logging :as log]
            [jaque.util :as util]
            [clojure.core.async :refer [<! put! sub chan go-loop]])
  (:import (java.nio.file Paths)
           (com.google.common.io MoreFiles RecursiveDeleteOption)
           (net.frodwith.jaque
             data.Noun
             data.List
             data.Atom)))

(defn- rel [base f]
  (.relativize base (Paths/get (.toURI f))))

(defn- visible? [base ctx e]
  (let [f (:file e)
        p (rel base f)]
    (and (not-any? #(.startsWith (.toString %) ".") p)
         (or (not (.exists f))
             (.isFile f)))))

(defn- handle-notify [base wire nama poke ctx e]
  (let [path (rel base (:file e))
        in   (util/path-to-noun base path)
        ovo (noun [wire :into nama Atom/NO in 0])]
    (put! poke ovo)))

(defn start [{effects :effect-pub, mnt :mount-dir, sen :sen, poke :poke-channel}]
  (let [syncs (chan)
        mnp   (Paths/get (.toURI mnt))
        wire  (noun [0 :sync sen 0])
        w!    (fn [m nama]
                (let [nam (Atom/cordToString nama)
                      base (util/pier-path mnp nam)]
                  (if (contains? m nam)
                    m
                    (let [w (hawk/watch! [{:paths [(.toFile base)]
                                           :handler (partial handle-notify base wire nama poke)
                                           :filter (partial visible? base)}])]
                      (log/debug "mount" nam)
                      (assoc m nam w)))))]
    (sub effects wire syncs)
    (put! poke (noun [wire :boat 0]))
    (go-loop [watchers {}]
      (let [s (<! syncs)]
        (if (nil? s)
          (log/debug "fs shutting down")
          (let [ovo (.tail s)
                tag (.head ovo)
                dat (.tail ovo)]
            (recur
              (case (Atom/cordToString tag)
                "ergo"  (let [nama (.head dat)
                              nam  (Atom/cordToString nama)
                              base (util/pier-path mnp nam)]
                          (doseq [f (List. (.tail dat))]
                            (let [pax (.head f)
                                  umi (.tail f)
                                  pas (map #(Atom/cordToString %) (List. pax))
                                  fil (util/path-seq-to-file base pas)]
                              (if (Noun/isCell umi)
                                (let [c (Atom/toByteArray (.tail (.tail (.tail umi))))]
                                  (util/write-file fil c))
                                (when (.exists fil) (.delete fil)))))
                          (w! watchers nama))
                "ogre"  (let [nam  (Atom/cordToString dat)
                              wat  (get watchers nam)
                              base (util/pier-path mnp nam)]
                          (if (nil? wat)
                            watchers
                            (do (hawk/stop! wat)
                                (MoreFiles/deleteRecursively base (into-array RecursiveDeleteOption nil))
                                (log/debug "unmount" nam)
                                (dissoc watchers nam))))
                "hill"  (reduce (fn [m mnt]
                                  (let [nam (Atom/cordToString mnt)
                                        can (util/dir-can (.toFile (util/pier-path mnp nam)))]
                                    (put! poke (noun [wire :into mnt 0 can]))
                                    (log/debug "hill" nam)
                                    (w! m mnt)))
                                watchers
                                (List. dat))
                (do (log/error "unknown sync effect:" (Noun/toString ovo))
                    watchers)))))))))

