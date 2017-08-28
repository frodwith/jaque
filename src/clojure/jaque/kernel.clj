(ns jaque.kernel
  (:use jaque.noun)
  (:require 
    [jaque.util :as util]
    [jaque.terminal :as terminal]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clojure.tools.logging :as log]
    [clojure.core.async :refer [>! >!! <! onto-chan go go-loop chan]])
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
      truffle.Context
      data.List
      data.Time
      data.Noun
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

(defn- boot-poke [k ech event]
  (let [ctx (:context k)
        old (.caller ctx)]
    (set! (.caller ctx) 
          (reify Caller
            (kernel [this gate-name sample]
              (kernel-call k gate-name sample))
            (slog [this tank]
              (>!! (:slog k) tank))))
    (let [res (slam k (kernel-axis k 42) [(Time/now) event])
          eff (.head res)
          arv (.tail res)]
      (set! (.caller ctx) old)
      (>!! ech eff)
      (assoc k :arvo arv))))

(defn- term-curds [in out]
  (go-loop []
    (let [effs (<! in)
          eseq (List. effs)]
      (doseq [eff eseq]
        (log/debugf "effect: %s" (Noun/toString eff))
        (if (Noun/equals (.head eff) (noun [0 :term :1 0]))
          (>! out (.tail eff))
          (log/warnf "unhandled effect: %s" (Noun/toString eff))))
      (recur))))

(defn- feed [ch]
  (reify Consumer
    (accept [this arg] (go (>! ch arg)))))

; poke: we read poke nouns and feed them to arvo
; eff:  we write lists of effects, doing no dispatching
; tank: we write tanks to be printed somewhere
(defn start [poke eff tank {:keys [jets profile pill sync-dir pier-dir]}]
  (let [ctx (Context. jets profile)
        sys (PrevalentSystem. ctx (feed tank) (feed eff))
        fac (doto (PrevaylerFactory.)
              (.configurePrevalentSystem sys)
              (.configurePrevalenceDirectory pier-dir)
              (.configureTransactionDeepCopy false))
        pre (.create fac)]
    (when (nil? (.arvo sys))
      (let [k (boot ctx pill tank)
            k (-> k
                  (boot-poke eff [[0 :newt (:sen k) 0] :barn 0])
                  (boot-poke eff [[0 :term :1 0] :boot :sith 0 0 0])
                  (boot-poke eff [0 :verb 0])
                  (boot-poke eff (home-sync k sync-dir)))]
        (.execute pre (Boot. (.locations ctx)
                             (:arvo k)
                             (:now k)
                             (:wen k)
                             (:sen k)
                             (:sev k)))))
    (go-loop []
      (.execute pre (Poke. (<! poke)))
      (recur))))

(defn run []
  (let [poke (chan)
        eff  (chan)
        curd (chan)
        tank (chan)
        jets (util/read-jets "/home/pdriver/code/jaque/maint/jets.edn")
        pill (util/read-jam "/home/pdriver/code/jaque/maint/urbit.pill")
        adir "/home/pdriver/code/urbit-maint/arvo"
        pdir "/tmp/jaque-pier"]
    (term-curds eff curd)
    (terminal/start tank curd poke)
    (start poke eff tank {:jets jets
                          :profile false
                          :pill pill
                          :sync-dir adir
                          :pier-dir pdir})
    {:poke poke
     :eff  eff
     :tank tank}))
