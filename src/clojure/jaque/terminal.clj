(ns jaque.terminal
  (:use jaque.noun)
  (:require [clojure.java.io :as io]
            [clojure.core.async :refer [>! >!! <! go go-loop chan timeout alt!]]
            [clojure.string :as string]
            [clojure.tools.logging :as log])
  (:import (java.io File)
           (net.frodwith.jaque.data
             Atom
             List
             Tape
             Tank
             Noun)
           (javax.sound.midi MidiSystem Synthesizer)
           (com.googlecode.lanterna
             input.KeyType
             screen.Screen
             screen.TerminalScreen
             terminal.ExtendedTerminal
             terminal.DefaultTerminalFactory
             TerminalPosition
             TextCharacter)))

;; FIXME there is a superious record with just a lanterna object in it, that i
;; don't pass around correctly - i just use it for side effects and hang
;; updating references.

(defn- scrub-control-chars [s]
  (string/replace s #"\x1b\[[0-9;]*[mG]" ""))

(defn- index-str 
  ([s with] (map vector s with))
  ([s] (index-str s (range 0 (.length s)))))

(defprotocol DillTerminal
  (spinner-tick [this caption i])
  (read-belt [this])
  (clr [this])
  (hop [this to-column])
  (line [this text])
  (scroll [this])
  (save [this path-seq content-bytes])
  (link [this url])
  (dimensions [this])
  (commit [this]))

(defrecord Lanterna [screen]
  DillTerminal
  (spinner-tick [this caption i]
    (let [[cols rows] (dimensions this)
          row  (dec rows)
          spin-char (case i 0 \|
                            1 \/
                            2 \-
                            3 \\)
          full (str spin-char \u00AB caption \u00BB)]
      (doseq [[c i] (index-str full)]
        (.setCharacter screen i row (TextCharacter. c)))
      this))
  (read-belt [this]
    (let [stroke (.readInput screen)
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
                       :else ;beep?
                         (read-belt this))]
      (noun [:belt belt])))
  (clr [this]
    (.clear screen)
    this)
  (hop [this to-column]
    (let [pos (.getCursorPosition screen)]
      (.setCursorPosition screen (.withColumn pos to-column)))
    this)
  (line [this text]
    (let [size (.getTerminalSize screen)
          row  (dec (.getRows size))
          cln  (scrub-control-chars text)
          len  (.length cln)]
      (doseq [[c i] (index-str cln)]
        (.setCharacter screen i row (TextCharacter. c)))
      (doseq [i (range len (.getColumns size))]
        (.setCharacter screen i row (TextCharacter. \space)))
      (.setCursorPosition screen (TerminalPosition. len row))
      this))
  (scroll [this]
    (let [bottom (dec (.getRows (.getTerminalSize screen)))]
      (.scrollLines screen 0 bottom 1)
      (.setCursorPosition screen (TerminalPosition. 0 bottom))
      this))
  (save [this path-seq content-bytes]
    (with-open [out (io/output-stream (io/file (string/join File/pathSeparator path-seq)))]
      (.write out content-bytes))
    this)
  (link [this url]
    (-> this
        (line url)
        (scroll)))
  (dimensions [this]
    (let [s (.getTerminalSize screen)]
      [(.getColumns s) (.getRows s)]))
  (commit [this]
    (.refresh screen)
    this))

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

(defn- spinner [term spin]
  (go-loop [[cap i] [0 0]]
    (recur
      (if (Atom/isZero cap)
        [(<! spin) 0]
        (alt! spin          ([cap] [cap 0])
              (timeout 500) (let [ni (if (< i 3) (inc i) 0)]
                                (commit (spinner-tick term (Atom/cordToString cap) i))
                                [cap ni]))))))

(defn- make-lanterna []
  (let [f (DefaultTerminalFactory.)
        t (.createTerminal f)]
    (when (isa? t ExtendedTerminal)
      (.maximize t))
    (let [s (TerminalScreen. t)]
      (.startScreen s)
      (.doResizeIfNecessary s)
      (map->Lanterna {:screen s}))))

(defn- egger [term poke beep spin curd]
  (go-loop []
    (let [egg (<! curd)
          tag (Atom/cordToString (.head egg))]
      (case tag
        "init"  (let [[rows cols] (dimensions term)
                      wir  [0 :term :1 0]
                      blew (noun [wir :blew rows cols]) 
                      hail (noun [wir :hail 0])]
                  (go (>! poke blew)
                      (>! poke hail)))
        "blit"  (commit
                  (reduce 
                    (fn [term ovum]
                      (let [tag (Atom/cordToString (.head ovum))
                            data (.tail ovum)]
                        (log/debugf "blit: %s" (Noun/toString ovum))
                        (case tag
                          "bee" (do (go (>! spin data))
                                    term)
                          "bel" (do (go (>! beep :beep))
                                    term)
                          "clr" (clr term)
                          "hop" (hop term (Atom/expectLong data))
                          "lin" (line term (Tape/toString data))
                          "mor" (scroll term)
                          "sav" (let [pax (List. (.head data))
                                      pad (Atom/toByteArray (.tail data))]
                                  (save term pax pad))
                          "sag" (let [pax (List. (.head data))
                                      pad (Atom/toByteArray (Atom/jam (.tail data)))]
                                  (save term pax pad))
                          "url" (link term data)
                          (do (log/warnf "unhandled blit: %s" tag)
                              term))))
                    term (List. (.tail egg))))
        (do (log/warnf "unhandled terminal effect: %s" tag)
            term)))
    (recur)))

(defn- listen [term poke]
  (.start (Thread. #(loop []
                      (>!! poke (noun [[0 :term :1 0] (read-belt term)]))
                      (recur)))))

(defn- wall-seq [wall]
  (map #(Tape/toString %) (List. wall)))

(defn- tank-seq [width tank]
  (wall-seq (Tank/wash 0 width tank)))

(defn- dump-tank [term tank]
  (log/debug (string/join \newline (tank-seq 80 tank)))
  (let [[cols _] (dimensions term)]
    (doseq [string (tank-seq (long cols) tank)]
      (commit (scroll (line term string))))))

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
