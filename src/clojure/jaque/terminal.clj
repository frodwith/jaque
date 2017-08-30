(ns jaque.terminal
  (:use jaque.noun)
  (:require [clojure.java.io :as io]
            [clojure.core.async :refer [>! >!! <! go go-loop chan timeout alt!]]
            [clojure.string :as string]
            [clojure.tools.logging :as log])
  (:import (java.io File)
           (net.frodwith.jaque
             LineWidthHackScreen
             data.Atom
             data.List
             data.Tape
             data.Tank
             data.Noun)
           (javax.sound.midi MidiSystem Synthesizer)
           (com.googlecode.lanterna
             input.KeyType
             screen.TerminalScreen
             terminal.ExtendedTerminal
             terminal.DefaultTerminalFactory
             TerminalPosition
             TextCharacter)))

(defn- index-str 
  ([s with] (map vector s with))
  ([s] (index-str s (range 0 (.length s)))))

(defn- scrub-control-chars [s]
  (string/replace s #"\x1b\[[0-9;]*[mG]" ""))

(defprotocol BlitSink
  (spin [this caption i])
  (clr [this])
  (hop [this to-column])
  (line [this text])
  (scroll [this])
  (save [this path-seq content-bytes])
  (link [this url])
  (dimensions [this])
  (commit [this]))

(defprotocol BeltSource
  (read-belt [this]))

(extend-type LineWidthHackScreen
  BeltSource 
  (read-belt [this]
    (let [stroke (.readInput this)
          stype  (.getKeyType stroke)
          belt   (cond (= stype KeyType/ArrowDown)  [:aro :d]
                       (= stype KeyType/ArrowLeft)  [:aro :l]
                       (= stype KeyType/ArrowRight) [:aro :r]
                       (= stype KeyType/ArrowUp)    [:aro :u]
                       (= stype KeyType/Backspace)  [:bac 0]
                       (= stype KeyType/Delete)     [:del 0]
                       (= stype KeyType/Enter)      [:ret 0]
                       (= stype KeyType/Character) 
                       (let [c (long (.getCharacter stroke))]
                         (cond (.isCtrlDown stroke) [:ctl c]
                               (.isAltDown stroke)  [:met c]
                               :else                [:txt c 0]))
                       :else nil)]
      (if (nil? belt)
        (recur this)
        (noun [:belt belt]))))
  BlitSink   
  (clr [this] (.clear this))
  (commit [this] (.refresh this))
  (link [this url] (doto this (line url) scroll))
  (hop [this col]
    (let [pos (.getCursorPosition this)]
      (.setCursorPosition this (.withColumn pos (- col (.stripChars ^LineWidthHackScreen this))))))
  (save [this path-seq content-bytes]
    (with-open [out (io/output-stream (io/file (string/join File/pathSeparator path-seq)))]
      (.write out content-bytes)))
  (dimensions [this]
    (let [s (.getTerminalSize this)]
      [(.getColumns s) (.getRows s)]))
  (spin [this caption i]
    (let [[cols rows] (dimensions this)
          row  (dec rows)
          spin (case i 0 \| 1 \/ 2 \- 3 \\)
          full (str spin \u00AB caption \u00BB)]
      (doseq [[c i] (index-str full)]
        (.setCharacter this i row (TextCharacter. c)))))
  (scroll [this]
    (let [bottom (dec (.getRows (.getTerminalSize this)))
          newpos (TerminalPosition. 0 bottom)]
      (doto this
        (.scrollLines 0 bottom 1)
        (.setCursorPosition newpos))))
  (line [this text]
    (let [[cols rows] (dimensions this)
          row  (dec rows)
          cln  (scrub-control-chars text)
          len  (.length cln)]
      (doseq [[c i] (index-str cln)]
        (.setCharacter this i row (TextCharacter. c)))
      (doseq [i (range len cols)]
        (.setCharacter this i row (TextCharacter. \space)))
      (set! (. this stripChars) (- (.length text) len)))))

(defn- beeper []
  (let [ch    (chan)
        synth (doto (MidiSystem/getSynthesizer) .open)
        mch   (aget (.getChannels synth) 0)]
    (go-loop []
      (<! ch)
      (.noteOn mch 67 200)
      (<! (timeout 100))
      (.noteOff mch 67)
      (recur))
    ch))

(defn- spinner [sink ch]
  (go-loop [[cap i] [0 0]]
    (recur
      (if (Atom/isZero cap)
        [(<! ch) 0]
        (alt! ch            ([cap] [cap 0])
              (timeout 500) (let [ni (if (< i 3) (inc i) 0)]
                              (doto sink
                                (spin (Atom/cordToString cap) i)
                                (commit))
                              [cap ni]))))))

(defn- make-lanterna []
  (let [f (DefaultTerminalFactory.)
        t (.createTerminal f)]
    (when (isa? t ExtendedTerminal)
      (.maximize t))
    (let [s (doto (LineWidthHackScreen. t)
              (.startScreen)
              (.doResizeIfNecessary))
          [cols rows] (dimensions s)]
      (.setCursorPosition s (TerminalPosition. 0 (dec rows)))
      s)))

(defn- blit-one [sink beep spin ovum]
  (let [tag (Atom/cordToString (.head ovum))
        data (.tail ovum)]
    (case tag
      "bee" (go (>! spin data))
      "bel" (go (>! beep :beep))
      "clr" (clr sink)
      "hop" (hop sink (Atom/expectLong data))
      "lin" (line sink (Tape/toString data))
      "mor" (scroll sink)
      "sav" (let [pax (List. (.head data))
                  pad (Atom/toByteArray (.tail data))]
              (save sink pax pad))
      "sag" (let [pax (List. (.head data))
                  pad (Atom/toByteArray (Atom/jam (.tail data)))]
              (save sink pax pad))
      "url" (link sink data)
      (log/warnf "unhandled blit: %s" tag))))

(defn- egger [sink poke beep spin curd]
  (go-loop []
    (let [egg (<! curd)
          tag (Atom/cordToString (.head egg))]
      (case tag
        "init"  (let [[rows cols] (dimensions sink)
                      wir  [0 :term :1 0]
                      blew (noun [wir :blew rows cols]) 
                      hail (noun [wir :hail 0])]
                  (go (>! poke blew)
                      (>! poke hail)))
        "blit"  (do (doseq [ovum (List. (.tail egg))]
                      (blit-one sink beep spin ovum))
                    (commit sink))
        (log/warnf "unhandled terminal effect: %s" tag)))
    (recur)))

(defn- listen [source poke]
  (.start (Thread. #(loop []
                      (let [belt (read-belt source)
                            ovum (noun [[0 :term :1 0] belt])]
                        (>!! poke ovum))
                      (recur)))))

(defn- wall-seq [wall]
  (map #(Tape/toString %) (List. wall)))

(defn- tank-seq [width tank]
  (wall-seq (Tank/wash 0 width tank)))

(defn- dump-tank [sink tank]
  (log/debug (string/join \newline (tank-seq 80 tank)))
  (let [[cols _] (dimensions sink)]
    (doseq [string (tank-seq (long cols) tank)]
      (doto sink
        (line string)
        (scroll)))
    (commit sink)))

; we read tanks from tank (probably from slog hints)
; we read arvo curds from curd (for wire [0 :term :1 0] ONLY)
; we send pokes to arvo on poke 
(defn start [tank curd poke]
  (let [beep (beeper)
        init (chan)
        spin (chan)
        term (make-lanterna)]
    (egger term poke beep spin curd)
    (listen term poke)
    (spinner term spin)
    (go-loop []
      (dump-tank term (<! tank))
      (recur))))
