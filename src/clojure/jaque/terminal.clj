(ns jaque.terminal
  (:use jaque.noun)
  (:require [clojure.java.io :as io]
            [clojure.core.async :refer [<! >! >!! put! alt! close! sub go go-loop chan timeout]]
            [clojure.string :as string]
            [jaque.util :as util]
            [clojure.tools.logging :as log])
  (:import (net.frodwith.jaque
             JaqueScreen
             data.Atom
             data.Cell
             data.List
             data.Tape
             data.Tank
             data.Noun)
           (javax.sound.midi MidiSystem Synthesizer MidiChannel)
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
  (spin [this caption])
  (clr [this])
  (hop [this to-column])
  (line [this text keep?])
  (scroll [this])
  (save [this root-dir path-seq content-bytes])
  (link [this url])
  (dimensions [this])
  (restore [this])
  (commit [this]))

(defprotocol BeltSource
  (read-belt [this]))

(extend-type JaqueScreen
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
        (if (= stype KeyType/EOF)
          nil
          (recur this))
        (noun [:belt belt]))))
  BlitSink   
  (clr [this] (.clear this))
  (commit [this] (.refresh this))
  (link [this url] (doto this (line url false) scroll))
  (hop [this col]
    (let [pos (.getCursorPosition this)
          scr ^JaqueScreen this]
      (set! (. scr lastHop) col)
      (.setCursorPosition this (.withColumn pos (- col (.stripChars ^JaqueScreen this))))))
  (save [this root-dir path-seq content-bytes]
    (let [fil (util/path-seq-to-file root-dir path-seq)]
      (util/write-file fil content-bytes)))
  (restore [this]
    (let [scr ^JaqueScreen this]
      (line this (.lastLine scr) false)
      (hop this (.lastHop scr))))
  (dimensions [this]
    (let [s (.getTerminalSize this)]
      [(.getColumns s) (.getRows s)]))
  (spin [this caption]
    (let [[cols rows] (dimensions this)
          row  (dec rows)
          spin (.getSpinChar ^JaqueScreen this)
          full (str spin \u00AB caption \u00BB)]
      (doseq [[c i] (index-str full)]
        (let [tc (if (Character/isISOControl c) \? c)]
          (.setCharacter this i row (TextCharacter. tc))))))
  (scroll [this]
    (let [bottom (dec (.getRows (.getTerminalSize this)))
          newpos (TerminalPosition. 0 bottom)]
      (doto this
        (.scrollLines 0 bottom 1)
        (.setCursorPosition newpos))))
  (line [this text keep?]
    (let [[cols rows] (dimensions this)
          row  (dec rows)
          cln  (scrub-control-chars text)
          scr  ^JaqueScreen this
          len  (.length cln)]
      (doseq [[c i] (index-str cln)]
        (.setCharacter this i row (TextCharacter. c)))
      (doseq [i (range len cols)]
        (.setCharacter this i row (TextCharacter. \space)))
      (when keep? (set! (. scr lastLine) text))
      (set! (. scr stripChars) (- (.length text) len)))))

(defn- make-lanterna []
  (let [f (DefaultTerminalFactory.)
        t (.createTerminal f)]
    (when (isa? t ExtendedTerminal)
      (.maximize t))
    (let [s (doto (JaqueScreen. t)
              (.startScreen)
              (.doResizeIfNecessary))
          [cols rows] (dimensions s)]
      (.setCursorPosition s (TerminalPosition. 0 (dec rows)))
      s)))

(defn- wall-seq [wall]
  (map #(Tape/toString %) (List. wall)))

(defn- tank-seq [width tank]
  (wall-seq (Tank/wash 0 width tank)))

(defn- handle-tank [sink tank]
  (log/debug (string/join \newline (tank-seq 80 tank)))
  (let [[cols _] (dimensions sink)]
    (doseq [string (tank-seq (long cols) tank)]
      (doto sink
        (line string false)
        (scroll)))
    (commit sink)))

(defn- handle-beep [^MidiChannel c]
  (go
    (.noteOn c 67 200)
    (<! (timeout 100))
    (.noteOff c 67)))

(defn- blit-one [sink save-dir beep spin ovum]
  (let [tag (Atom/cordToString (.head ovum))
        data (.tail ovum)]
    (case tag
      "bee" (put! spin data)
      "bel" (put! beep :beep)
      "clr" (clr sink)
      "hop" (hop sink (Atom/expectLong data))
      "lin" (line sink (Tape/toString data) true)
      "mor" (scroll sink)
      "sav" (let [pax (List. (.head data))
                  pad (Atom/toByteArray (.tail data))]
              (save sink save-dir pax pad))
      "sag" (let [pax (List. (.head data))
                  pad (Atom/toByteArray (Atom/jam (.tail data)))]
              (save sink save-dir pax pad))
      "url" (link sink data)
      (log/warnf "unhandled blit: %s" tag))))

(defn- handle-egg [sink poke save-dir beep spin ^Cell ovum]
  (let [egg (.tail ovum)
        tag (Atom/cordToString (.head egg))]
    (case tag
      "init" (let [[rows cols] (dimensions sink)
                   wir  [0 :term :1 0]]
               (put! poke (noun [wir :blew rows cols]))
               (put! poke (noun [wir :hail 0])))
      "blit" (do (doseq [ovum (List. (.tail egg))]
                   (blit-one sink save-dir beep spin ovum))
                 (commit sink))
      "logo" (close! poke)
      (log/warnf "unhandled terminal effect: %s" tag))))

(defn- make-wire [id]
  (noun [0 :term id 0]))

(defn- spinup [sink ch]
  (go-loop [cap 0]
    (let [c (if (Atom/isZero cap)
              (do (restore sink)
                  (commit sink)
                  (<! ch))
              (alt! ch 
                    ([cap]
                     (if (nil? cap)
                       false
                       cap))
                    (timeout 500) 
                    (do (spin sink (Atom/cordToString cap))
                        (commit sink)
                        cap)))]
      (if c
        (recur c)
        (log/debug "spinner shutdown")))))

(defn- listen [source poke kth wire]
  (let [scr ^JaqueScreen source]
    (.start
      (Thread. #(loop []
                  (let [belt (read-belt source)]
                    (if (nil? belt)
                      (log/debug "keystroke listener shutdown")
                      (do (if (Noun/equals (noun [:belt :ctl (long \c)]) belt)
                            (.interrupt kth)
                            (>!! poke (noun [wire belt])))
                          (recur)))))))))

(defn start [{effects :effect-pub, id :terminal-id, save-dir :save-root,
              tank :tank-channel, poke :poke-channel, kth :kernel-thread}]
  (let [beep    (chan)
        spin    (chan)
        eggs    (chan)
        wire    (make-wire id)
        sink    (make-lanterna)
        spinner (spinup sink spin)
        do-beep (partial handle-beep
                  (let [synth (doto (MidiSystem/getSynthesizer) .open)]
                    (aget (.getChannels synth) 0)))
        do-egg  (partial handle-egg sink poke save-dir beep spin)
        do-tank (partial handle-tank sink)]
    (sub effects wire eggs)
    (listen sink poke kth wire)
    (let [[cols rows] (dimensions sink)
          wire        [0 :term :1 0]]
      (put! poke (noun [wire :harm 0]))
      (put! poke (noun [wire :blew rows cols]))
      (put! poke (noun [wire :hail 0])))
    (go
      (loop []
        (when (alt! beep (do (do-beep)
                             true)
                    eggs ([egg] (and (not (nil? egg))
                                     (do (do-egg egg)
                                         true)))
                    tank ([tac] (and (not (nil? tac))
                                     (do (do-tank tac)
                                         true))))
          (recur)))
      (.close sink)
      (close! tank)
      (close! beep)
      (close! spin)
      (<! spinner)
      (log/debug "terminal shutdown"))))
