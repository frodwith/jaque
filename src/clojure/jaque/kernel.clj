(ns jaque.kernel
  (:use jaque.noun)
  (:require 
    [jaque.util :as util]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clojure.tools.logging :as log]
    [clojure.core.async :as async])
  (:import 
    (java.nio.file Paths)
    (java.util.function Consumer)
    (org.prevayler
      foundation.serialization.JavaSerializer
      PrevaylerFactory)
    (net.frodwith.jaque
      Caller
      prevayler.PrevalentSystem
      prevayler.Boot
      prevayler.Poke
      prevayler.Wake
      truffle.Context
      data.List
      data.Tape
      data.Time
      data.Noun
      data.Cell
      data.Atom)))

(defn- kernel-axis [k axis]
  (.nock (:context k)
         (:arvo k)
         (noun [9 axis 0 1])))

(defn- slam [k gate sample]
  (.wrapSlam (:context k) gate (noun sample)))

(defn- wish [k s]
  (slam k (kernel-axis k 20) (Atom/stringToCord s)))

(defn- kernel-call [k n s]
  (slam k (wish k n) s))

(defn- boot [ctx pill slog]
  (let [ken (.head pill)
        roc (.tail pill)
        cor (.nock ctx 0 ken) ; "to bind jets"
        da  (Time/now)
        uv  (long (Noun/mug da))
        k   {:sev uv
             :now da
             :arvo roc
             :context ctx
             :slog slog}]
    (assoc k
      :wen (kernel-call k "scot" [:da da])
      :sen (kernel-call k "scot" [:uv uv]))))

(defn- home-sync [k dir]
  (let [wire [0 :sync (:sen k) 0]]
    (noun [wire :into 0 0 (util/dir-can dir)])))

(defn- dispatch! [ch effects]
  (async/go
    (doseq [e (List. effects)]
      (async/>! ch e))))

(defn dispatch!! [ch effects]
  (doseq [e (List. effects)]
    (async/>!! ch e)))

(defn- boot-poke [k ech event]
  (let [ctx (:context k)
        tir (noun [0 :term :1 0])
        evt (noun event)
        old (.caller ctx)]
    (set! (.caller ctx) 
          (reify Caller
            (kernel [this gate-name sample]
              (kernel-call k gate-name sample))
            (slog [this tank]
              (async/put! (:slog k) tank))))
    (async/>!! ech (noun [tir [:blit [:bee (.head (.tail (.head evt)))] 0]]))
    (let [res (slam k (kernel-axis k 42) [(Time/now) evt])
          eff (.head res)
          arv (.tail res)]
      (set! (.caller ctx) old)
      (async/>!! ech (noun [tir [:blit [:bee 0] 0]]))
      (dispatch!! ech eff)
      (assoc k :arvo arv))))

(defn- timers [ua ub]
  (if (Noun/isCell ua)
    (if (Noun/isCell ub)
      (let [a (.tail ua)
            b (.tail ub)
            c (Atom/compare a b)]
        (case c
          -1 [a [:ames]]
          0  [a [:ames :behn]]
          1  [b [:behn]]))
      [(.tail ua) [:ames]])
    (if (Noun/isCell ub)
      [(.tail ub) [:behn]]
      nil)))

(defn start [{pro :profile, 
              jet :jet-path, syn :sync-path, pir :pier-path, pil :pill-path
              eff :effect-channel, tac :tank-channel, pok :poke-channel}]
  (let [pdir (util/pier-path pir ".urb" "prevayler")
        fac (doto (PrevaylerFactory.)
              (.configurePrevalentSystem (PrevalentSystem.))
              (.configurePrevalenceDirectory (.toString pdir))
              (.configureTransactionDeepCopy false))
        pre (.create fac)
        tir (noun [0 :term :1 0])
        tank-cb (reify Consumer
                  (accept [this t] (async/>!! tac t)))
        eff-cb  (reify Consumer 
                  (accept [this es] (dispatch!! eff es)))
        init    #(do (when (.execute pre (Wake. (util/read-jets jet) tank-cb eff-cb pro))
                       (let [ctx (.context (.prevalentSystem pre))
                             k (boot ctx (util/read-jam pil) tac)
                             k (-> k
                                   (boot-poke eff [[0 :newt (:sen k) 0] :barn 0])
                                   (boot-poke eff [tir :boot :sith 0 0 0])
                                   ;(boot-poke eff [0 :verb 0])
                                   (boot-poke eff (home-sync k syn)))]
                         (.execute pre (Boot. (.locations ctx)
                                              (:arvo k)
                                              (:now k)
                                              (:wen k)
                                              (:sen k)
                                              (:sev k)))))
                     (.sen (.prevalentSystem pre)))
        kth      (Thread.
                   (fn again []
                      (try
                        (loop []
                          (let [sys (.prevalentSystem pre)
                                ame (.keep sys (noun :ames))
                                ben (.keep sys (noun :behn))
                                tim (timers ame ben)
                                wat (if (nil? tim) 
                                      (do (log/debug "no timers")
                                      [pok]
                                      )(do (log/debug "timers")
                                      [pok (async/timeout (Time/millisecondsUntil (first tim)))])
                                      )
                                [v ch] (async/alts!! wat)]
                            (if-not (= ch pok)
                              (do (doseq [k (second tim)]
                                  (async/put! pok (noun [[0 k 0] :wake 0])))
                                  (recur))
                              (if (= v :ignore)
                                (recur)
                                (if (nil? v)
                                  (do (.takeSnapshot pre)
                                      (.close pre)
                                      (async/close! eff)
                                      (log/debug "kernel shutdown"))
                                  (do (async/>!! eff (noun [tir [:blit [:bee (.head (.tail (.head v)))] 0]]))
                                      (let [ctx (.context sys)
                                            arv (.arvo sys)
                                            tx  (Poke. v (Time/now))]
                                        (.deliver tx ctx arv) ; throws on delivery failure
                                        ; drop-if-not-completed
                                        (.execute pre tx))
                                      (async/>!! eff (noun [tir [:blit [:bee 0] 0]]))
                                      (recur)))))))
                        (catch InterruptedException e
                          ; possibly a core.async bug, but after an interrupt
                          ; the next thing is ignored
                          (async/>!! pok :ignore)
                          (async/>!! eff (noun [tir :blit [[:bel 0] [:mor 0]
                                                           [:lin (Tape/fromString "interrupt")]
                                                           [:mor 0] 0]]))
                          (again)))))]
    [init kth]))
;                cal ([req]
;                     (if (nil? req)
;                       false
;                       (let [[gate-name sample respond] req]
;                         (>! respond (.externalCall sys pre gate-name sample))
;                         true))))
