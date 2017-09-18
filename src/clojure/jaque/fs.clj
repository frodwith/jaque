(ns jaque.fs
  (:use jaque.noun)
  (:require [hawk.core :as hawk]
            [clojure.tools.logging :as log]
            [jaque.util :as util]
            [clojure.core.async :refer [<! put! sub chan go-loop]])
  (:import (java.nio.file Paths)
           (net.frodwith.jaque
             data.Noun
             data.List
             data.Atom)))

(defn visible-file? [base ctx e]
  (let [f (:file e)
        p (.relativize base (Paths/get (.toURI f)))]
    (and (.isFile f)
         (not-any? #(.startsWith (.toString %) ".") p))))

(defn handle-notify [base poke ctx e]
  (if (= (:kind e) :modify)
    (let [path (.relativize base (Paths/get (.toURI (:file e))))
          in   (util/path-to-noun base path)
          ovo (noun [wire :into nama Atom/NO in 0])]
      (put! poke ovo))
    (log/debug "handler" (str e))))

(defn start [{effects :effect-pub, mnt :mount-dir, sen :sen, poke :poke-channel}]
  (let [syncs (chan)
        mnp   (Paths/get (.toURI mnt))
        wire  (noun [0 :sync sen 0])]
    (sub effects wire syncs)
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
                                  umi (.tail f)]
                              (util/write-file base 
                                (map #(Atom/cordToString %) (List. pax))
                                (if (Noun/isCell umi)
                                  (Atom/toByteArray (.tail (.tail (.tail umi))))
                                  (byte-array 0)))))
                          (log/debug "mount" nam)
                          (assoc watchers nam
                            (hawk/watch! [{:paths [(.toFile base)]
                                           :handler (partial handle-notify base)
                                           :filter (partial visible-file? base poke)}])))
                "ogre"  (let [nam (Atom/cordToString dat)
                              wat (get watchers nam)]
                          (if (nil? wat)
                            watchers
                            (do (hawk/stop! wat)
                                ; XX rm -rf mountpoint
                                (log/debug "unmount" nam)
                                (dissoc watchers nam))))
                "hill"  (do (log/debug (Noun/toString ovo))
                            watchers)
                (do (log/error "unknown sync effect:" (Noun/toString ovo))
                    watchers)))))))))

