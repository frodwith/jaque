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
    (java.security SecureRandom)
    (java.util.function Consumer)
    (org.prevayler
      foundation.serialization.JavaSerializer
      PrevaylerFactory)
    (net.frodwith.jaque
      Caller Interrupt
      prevayler.PrevalentSystem
      prevayler.Boot
      prevayler.Poke
      prevayler.Wake
      truffle.Context
      truffle.TypesGen
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

(defn- assoc-unit [m k u]
  (assoc m k
    (if (Noun/isCell u)
      (.tail u)
      0)))

(defn- boot [ctx pill slog id ticket]
  (let [ken (.head pill)
        roc (.tail pill)
        cor (.nock ctx 0 ken) ; "to bind jets"
        da  (Time/now)
        uv  (long (Noun/mug da))
        k   {:sev uv
             :now da
             :arvo roc
             :context ctx
             :slog slog}
        k   (assoc k :wen (kernel-call k "scot" [:da da])
                     :sen (kernel-call k "scot" [:uv uv]))
        k   (if (nil? id)
              k
              (assoc-unit k :who (kernel-call k "slaw" [:p (Atom/stringToCord id)])))
        k   (if (nil? ticket)
              k
              (assoc-unit k :gun (kernel-call k "slaw" [:p (Atom/stringToCord ticket)])))
        who (:who k)]
    (assoc k :galaxy (and (not (nil? who)) (TypesGen/isLong who) (= -1 (Atom/compare who 256))))))

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
    (try
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
      (assoc k :arvo arv))
    (catch Error e
      (.dumpHoonStack ctx)
      (throw e))
    (catch Exception e
      (.dumpHoonStack ctx)
      (throw e)))))

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

(defn- make-auth [k fake]
  (let [fat (if fake Atom/YES Atom/NO)]
    (if (:galaxy k)
      [:sith (:who k) [0 (if fake 0 (:gun k))] fat]
      [:make 0 11 (Atom/fromByteArray (.generateSeed (SecureRandom.) 64)) fat])))

(defn start [{pro :profile, id :identity, ticket :ticket, fake :fake,
              jet :jet-path, syn :sync-path, pir :pier-path, pil :pill-path
              eff :effect-channel, tac :tank-channel, pok :poke-channel}]
  (let [pdir (util/pier-path pir ".urb" "prevayler")
        fac (doto (PrevaylerFactory.)
              (.configurePrevalentSystem (PrevalentSystem.))
              (.configurePrevalenceDirectory (.toString pdir))
              (.configureTransactionDeepCopy false))
        pre (.create fac)
        tir (noun [0 :term :1 0])
        poke! (fn [v]
                (async/>!! eff (noun [tir [:blit [:bee (.head (.tail (.head v)))] 0]]))
                (let [sys (.prevalentSystem pre)
                      ctx (.context sys)
                      arv (.arvo sys)
                      tx  (Poke. v (Time/now))]
                  (.deliver tx ctx arv)
                  (.execute pre tx))
                (async/>!! eff (noun [tir [:blit [:bee 0] 0]])))
        tank-cb (reify Consumer
                  (accept [this t] (async/>!! tac t)))
        eff-cb  (reify Consumer 
                  (accept [this es] (dispatch!! eff es)))
        init    #(do (when (.execute pre (Wake. (util/read-jets jet) tank-cb eff-cb pro))
                       (let [ctx (.context (.prevalentSystem pre))
                             k (boot ctx (util/read-jam pil) tac id ticket)
                             k (boot-poke k eff [[0 :newt (:sen k) 0] :barn 0])
                             k (boot-poke k eff [tir :boot (make-auth k fake)])
                             k (if (:galaxy k)
                                 (boot-poke k eff (home-sync k syn))
                                 (if (:who k)
                                   (boot-poke k eff [tir :tick (:who k) (:gun k)])
                                   k))]
                         (.execute pre (Boot. (.locations ctx)
                                              (:arvo k)
                                              (:who k)
                                              (:now k)
                                              (:wen k)
                                              (:sen k)
                                              (:sev k)))))
                     (let [sys (.prevalentSystem pre)
                           who (.who sys)
                           galaxy (and (TypesGen/isLong who) (< who 256 ))]
                       [(.sen sys) who galaxy]))
        kth      (Thread.
                   (fn again []
                     (letfn [(handle-interrupt []
                               ; possibly a core.async bug, but after an interrupt
                               ; the next thing is ignored
                               (async/put! pok :ignore)
                               (async/>!! eff (noun [tir :blit [[:bee 0] [:bel 0] [:mor 0]
                                                                [:lin (Tape/fromString "interrupt")]
                                                                [:mor 0] 0]]))
                               (again))]
                       (try
                         (loop []
                           (let [sys (.prevalentSystem pre)
                                 ame (.keep sys (noun :ames))
                                 ben (.keep sys (noun :behn))
                                 tim (timers ame ben)
                                 wake! #(doseq [k (second tim)]
                                          (poke! (noun [[0 k 0] :wake 0])))
                                 recv! (fn [t]
                                         (let [wat (if (nil? t) [pok] [pok t])
                                               [v ch] (async/alts!! wat)]
                                           (if-not (= ch pok)
                                             (do (wake!) true)
                                             (case v
                                               :ignore true
                                               nil     (do (.takeSnapshot pre)
                                                           (.close pre)
                                                           (async/close! eff)
                                                           (log/debug "kernel shutdown")
                                                           false)
                                               (do (poke! v) true)))))]
                             (if (nil? tim)
                               (when (recv! nil) (recur))
                               (let [til (Time/gapMs (Time/now) (first tim))]
                                 (if (<= til 0)
                                   (do (wake!)
                                       (recur))
                                   (when (recv! (async/timeout til)) (recur)))))))
                         (catch Interrupt e
                           (handle-interrupt))
                         (catch InterruptedException e
                           (handle-interrupt))))))]
    [init kth]))
;                cal ([req]
;                     (if (nil? req)
;                       false
;                       (let [[gate-name sample respond] req]
;                         (>! respond (.externalCall sys pre gate-name sample))
;                         true))))
