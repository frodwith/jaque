(ns jaque.kernel
  (:use jaque.noun)
  (:require 
    [jaque.util :as util]
    [jaque.terminal :as terminal]
    [jaque.http :as http]
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

(defn- atom-from-file [file]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (io/copy (io/input-stream file) out)
    (-> out (.toByteArray)
        (Atom/fromByteArray Atom/LITTLE_ENDIAN))))

(defn- path-to-noun [base path]
  (let [nested   (map (fn [p]
                        (let [s (.toString p)]
                          (string/split s #"\.")))
                      path)
        cords    (map #(Atom/stringToCord %) (flatten nested))
        pax      (seq->it cords)
        file     (.toFile (.resolve base path))
        mim      [:text :plain 0]
        size     (.length file)
        contents (atom-from-file file)
        dat      [mim size contents]]
    (noun [pax 0 dat])))

(defn- home-sync [k dirpath]
  (let [f    (io/file dirpath)
        base (Paths/get (.toURI f))]
    (if (or (nil? f)
            (not (.isDirectory f)))
      (do (log/error "bad initial sync directory")
          k)
      (let [files (filter #(.isFile %) (file-seq f))
            rels  (map #(.relativize base (Paths/get (.toURI %))) files)
            vis   (filter (fn [path] (not-any? #(.startsWith (.toString %) ".") path)) rels)
            can   (seq->it (map (partial path-to-noun base) vis))
            pax   [0 :sync (:sen k) 0]
            fav   [:into 0 0 can]]
        (noun [pax fav])))))

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
            (register [this battery loc]
              nil)
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

(defn- by-wire [^Cell ovum]
  (.head ovum))

(defn start [{pro :profile, 
              jet :jet-path, syn :sync-path, pir :pier-path, pil :pill-path
              eff :effect-channel, tac :tank-channel, pok :poke-channel}]
  (let [fac (doto (PrevaylerFactory.)
              (.configurePrevalentSystem (PrevalentSystem.))
              (.configurePrevalenceDirectory pir)
              (.configureTransactionDeepCopy false))
        pre (.create fac)
        tir (noun [0 :term :1 0])
        ctx (Context. (util/read-jets jet) pro)
        tank-cb (reify Consumer
                  (accept [this t] (>!! tac t)))
        eff-cb  (reify Consumer
                  (accept [this es] (dispatch!! eff es)))]
    (if (.execute pre (Wake. ctx tank-cb eff-cb))
      (let [k (boot ctx (util/read-jam pil) tac)
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
    (go-loop []
      (let [p (<! pok)]
        (if (nil? p)
          (do (.takeSnapshot pre)
              (.close pre)
              (close! eff)
              (log/debug "kernel shutdown"))
          (do (>! eff (noun [tir [:blit [:bee (.head (.tail (.head p)))] 0]]))
              (.execute pre (Poke. p))
              (>! eff (noun [tir [:blit [:bee 0] 0]]))
              (recur)))))))
;                cal ([req]
;                     (if (nil? req)
;                       false
;                       (let [[gate-name sample respond] req]
;                         (>! respond (.externalCall sys pre gate-name sample))
;                         true))))

(defn run []
  (let [poke    (chan) ; shutdown by terminal (logo)
        tank    (chan) ; shutdown by terminal (logo)
        ;call   (chan)
        eff     (chan) ; shutdown by kernel
        ;http   (chan)
        effects (pub eff by-wire)
        term-ch (terminal/start effects (noun :1) tank poke "/tmp/jaque-put")
        kern-ch (start {:profile        false
                        :jet-path       "/home/pdriver/code/jaque/maint/jets.edn"
                        :pill-path      "/home/pdriver/code/jaque/maint/urbit.pill"
                        :sync-path      "/home/pdriver/code/urbit-maint/arvo"
                        :pier-path      "/tmp/jaque-pier"
                        :effect-channel eff
                        :tank-channel   tank
                        ;:call-channel   call
                        :poke-channel   poke})]
    (<!! term-ch)
    (<!! kern-ch)))
