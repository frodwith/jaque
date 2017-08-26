(ns jaque.terminal
  (:use jaque.noun)
  (:require [clojure.java.io :as io])
  (:require [clojure.core.async :as a])
  (:require [clojure.string :as string])
  (:require [clojure.tools.logging :as log])
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

(defn- scrub-control-chars [s]
  (letfn [(scrub-one [c]
            (if (or (< (int c) 32) (= (int c) 127))
              \?
              c))]
    (apply str (map scrub-one s))))

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
          len  (.length text)]
      (doseq [[c i] (index-str (scrub-control-chars text))]
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
  (let [ch    (a/chan)
        synth (doto (MidiSystem/getSynthesizer) .open)
        mch   (aget (.getChannels synth) 0)]
    (a/go-loop []
      (a/<! ch)
      (.noteOn mch 67 200)
      (a/<! (a/timeout 100))
      (.noteOff mch 67)
      (recur))
    ch))

(defn- spinner [term spin]
  (a/go-loop [[cap i] [0 0]]
    (recur
      (if (Atom/isZero cap)
        [(a/<! spin) i]
        (a/alt! spin            ([cap] [cap i])
                (a/timeout 500) (let [ni (if (< i 3) (inc i) 0)]
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

(defn- egger [init beep spin curd]
  (a/go-loop [term nil]
    (recur
      (let [egg (a/<! curd)
            tag (Atom/cordToString (.head egg))]
        (case tag
          "init"  (let [term (make-lanterna)]
                    (a/>! init term)
                    term)
          "blit"  (if (nil? term)
                    (do (log/error "blit to uninitialized terminal")
                        nil)
                    (let [data (.tail egg)
                          tag  (Atom/cordToString (.head data))]
                      (commit
                        (case tag
                          "bee" (do (a/>! spin (.tail data))
                                    term)
                          "bel" (do (a/>! beep :beep)
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
                              term)))))
          (do (log/warnf "unhandled terminal effect: %s" tag)
              term))))))

(defn- listen [term poke]
  (.start (Thread. #(loop []
                      (a/>!! poke (noun [[0 :term :1 0] (read-belt term)]))
                      (recur)))))

(defn- wall-seq [wall]
  (map #(Tape/toString %) (List. wall)))

(defn- tank-seq [width tank]
  (wall-seq (Tank/wash 0 width tank)))

(defn- dump-tank [term tank]
  (when (log/enabled? :info)
    (doseq [string (tank-seq 74 tank)]
      (log/infof "slog: %s" string)))
  (when-not (nil? term)
    (let [[_ cols] (dimensions term)]
      (doseq [string (tank-seq (long cols) tank)]
        (line term string)))))

; we read tanks from tank (probably from slog hints)
; we read arvo curds from curd (for wire [0 :term :1 0] ONLY)
; we send pokes to arvo on poke 
(defn start [tank curd poke]
  (let [beep (beeper)
        init (a/chan)
        spin (a/chan)]
    (egger init beep spin curd)
    (a/go-loop [term nil]
      (recur
        (a/alt! init  ([term]
                       (listen term poke)
                       (spinner term spin)
                       (let [[rows cols] (dimensions term)
                             pok #(noun [[0 :term :1 0] %])]
                         (a/>! poke (pok [:blew rows cols]))
                         (a/>! poke (pok [:hail 0]))
                         term))
                tank  ([t]
                       (dump-tank term t)
                       term))))))
