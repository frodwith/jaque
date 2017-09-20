(ns jaque.kernel
  (:use jaque.noun)
  (:require 
    [jaque.util :as util]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clojure.tools.logging :as log]
    [clojure.core.async :refer [put! <! >! <!! >!! alt! close! go go-loop chan pub]])
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
  (go
    (doseq [e (List. effects)]
      (>! ch e))))

(defn dispatch!! [ch effects]
  (doseq [e (List. effects)]
    (>!! ch e)))

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
              (put! (:slog k) tank))))
    (>!! ech (noun [tir [:blit [:bee (.head (.tail (.head evt)))] 0]]))
    (let [res (slam k (kernel-axis k 42) [(Time/now) evt])
          eff (.head res)
          arv (.tail res)]
      (set! (.caller ctx) old)
      (>!! ech (noun [tir [:blit [:bee 0] 0]]))
      (dispatch!! ech eff)
      (assoc k :arvo arv))))

; There are order issues here - if we're going to take the easy way out and
; always write events as they come in (not insensible), we need to have the
; terminal up (and accepting ctrl-c) so that a long poke can be interrupted
; -- alternatively, we can do the processing external to prevayler, and not
; send the poke to be persisted until we know if it has succeeded
; (probably correct)
; i don't want to persist the results, is the problem.

; i think basically we have to do the exception handling and re-poking of
; lameness in the transaction object, and simply time-out pokes during
; boot. in this way, if any poke was interrupted before the interrupt
; handler could say "nope, actually this was lame", (say by kill -9)
; we'll treat it like a network packet - we'll try to run it for a minute
; or so and then drop it. we can also make sure that a user attention key
; and a spinner are present so ctrl-c can cancel it.

; this is a compromise due to Prevayler's seemingly very opinionated idea that
; events should be written to the log as soon as they are recieved, and not
; after the transaction is finished. Since prevayler is open source, we could
; patch this in. I think the above compromise is acceptable, however.
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
                  (accept [this t] (>!! tac t)))
        eff-cb  (reify Consumer 
                  (accept [this es] (dispatch!! eff es)))
        init    #(do (if (.execute pre (Wake. (util/read-jets jet) tank-cb eff-cb pro))
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
                                              (:sev k))))
                       (>!! eff (noun [tir [:init 0]])))
                     (.sen (.prevalentSystem pre)))
        kth      (Thread.
                   #(loop []
                      (let [p (<!! pok)]
                        (if (nil? p)
                          (do (.takeSnapshot pre)
                              (.close pre)
                              (close! eff)
                              (log/debug "kernel shutdown"))
                          (do (>!! eff (noun [tir [:blit [:bee (.head (.tail (.head p)))] 0]]))
                              (.execute pre (Poke. p))
                              (>!! eff (noun [tir [:blit [:bee 0] 0]]))
                              (recur))))))]
    [init kth]))
;                cal ([req]
;                     (if (nil? req)
;                       false
;                       (let [[gate-name sample respond] req]
;                         (>! respond (.externalCall sys pre gate-name sample))
;                         true))))
