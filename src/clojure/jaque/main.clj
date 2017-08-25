(ns jaque.main
  (:require [clojure.java.io :as io])
  (:require [core.async :as a])
  (:require [clojure.string :as string])
  (:require [clojure.edn :as edn])
  (:require [clojure.tools.cli :as cli])
  (:require [jaque.noun :refer [noun noun? seq->it]])
  (:import (java.nio.file Paths)
           (javax.sound.midi MidiSystem Synthesizer)
           (java.io File)
           (com.googlecode.lanterna
             screen.Screen
             screen.TerminalScreen
             input.KeyType
             TerminalPosition
             terminal.ExtendedTerminal
             terminal.DefaultTerminalFactory
             TextCharacter)
           (net.frodwith.jaque.truffle.driver Arm NamedArm AxisArm)
           (net.frodwith.jaque
             Caller
             data.Atom
             data.Cell
             data.Noun
             data.List
             data.Time
             data.Tape
             data.Tank
             truffle.Context
             truffle.nodes.jet.ImplementationNode))
  (:gen-class
    :methods [#^{:static true} [lens [String] Object]]))

(def beeper
  (let [synth   (doto (MidiSystem/getSynthesizer) .open)
        channel (aget (.getChannels synth) 0)
        state   {:synth synth :channel channel}]
    (agent state)))

(defn do-beep [state]
  (let [channel (:channel state)]
    (.noteOn channel 67 200)
    (Thread/sleep 100)
    (.noteOff channel 67)
    state))

(defprotocol DillTerminal
  (spinner-tick [this caption])
  (read-belt [this])
  (bell [this])
  (clr [this])
  (hop [this to-column])
  (line [this text])
  (scroll [this])
  (save [this path-seq content-bytes])
  (link [this url])
  (dimensions [this])
  (commit [this]))

(defn index-str 
  ([s with] (map vector s with))
  ([s] (index-str s (range 0 (.length s)))))

(defn scrub-control-chars [s]
  (letfn [(scrub-one [c]
            (if (or (< (int c) 32) (= (int c) 127))
              \?
              c))]
    (apply str (map scrub-one s))))

(defrecord Lanterna [screen spinner-state]
  DillTerminal
  (spinner-tick [this caption]
    (let [size (.getTerminalSize screen)
          row  (dec (.getRows size))
          cols (.getColumns size)
          spin-char (case spinner-state
                      0 \|
                      1 \/
                      2 \-
                      3 \\)
          full (str spin-char \u00AB caption \u00BB)]
      (doseq [[c i] (index-str full (range (- cols (.length full)) cols))]
        (.setCharacter screen i row (TextCharacter. c)))
      (assoc this :spinner-state (if (< spinner-state 3)
                                   (inc spinner-state)
                                   0))))
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
  (bell [this]
    (send beeper do-beep)
    this)
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

(defn make-lanterna []
  (let [f (DefaultTerminalFactory.)
        t (.createTerminal f)]
    (when (isa? t ExtendedTerminal)
      (.maximize t))
    (let [s (TerminalScreen. t)]
      (.startScreen s)
      (.doResizeIfNecessary s)
      (map->Lanterna {:screen s
                      :spinner-state 0}))))

(defn read-jets [path]
	(let [jets  (edn/read (java.io.PushbackReader. (io/reader path)))
        klass (fn [class-name]
                (let [k (Class/forName class-name)]
                  (if (isa? k ImplementationNode)
                    k
                    (throw (Exception. (str "Invalid jet class" class-name))))))

        gates (map (fn [[label class-name]]
                     (AxisArm. label 2 (klass class-name)))
                   (:gates jets))
        arms  (map (fn [[label typ class-name]]
                     (let [k (klass class-name)]
                       (cond (contains? typ :name) 
                               (NamedArm. label (:name typ) k)
                             (contains? typ :axis)
                               (AxisArm. label (:axis typ) k)
                             :else (throw (Exception. (str "Invalid arm type" typ))))))
                   (:arms jets))]
    (into-array Arm (concat arms gates))))

(defn atom-from-file [file]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (io/copy (io/input-stream file) out)
    (-> out (.toByteArray)
        (Atom/fromByteArray Atom/LITTLE_ENDIAN))))

(defn read-jam [path]
  (Atom/cue (atom-from-file (io/as-file path))))

(defn path-to-noun [base path]
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

(defn input-loop [m]
  (let [belt (read-belt (:term m))
        pax  [0 :term :1 0]
        ovo  (noun [pax belt])]
    (-> m
        (plan ovo)
        (work)
        (input-loop))))

;; effect handling
(defn effect-handler []
  (let [synth    (doto (MidiSystem/getSynthesizer) .open)
        channel  (aget (.getChannels synth) 0)
        state    {:channel  channel}]
    (agent state)))

(defn remember-event-agent [state a]
  (assoc state :event-agent a))

(defn errln [& strings]
  (binding [*out* *err*]
    (apply println strings)))

(defn unhandled-wire [state wire]
  (errln "unhandled wire: " (Noun/toString wire))
  state)

(let [handler-map {(noun :bee) (fn [t a] (spinner-tick t a))
                   (noun :bel) (fn [t a] (bell t))
                   (noun :clr) (fn [t a] (clr t))
                   (noun :hop) (fn [t a] (hop t (Atom/expectLong a)))
                   (noun :lin) (fn [t a] (line t (Tape/toString a)))
                   (noun :mor) (fn [t a] (scroll t))
                   (noun :sav) (fn [t a]
                                 (let [pax (List. (.head a))
                                       pad (Atom/toByteArray (.tail a))]
                                   (save t pax pad)))
                   (noun :sag) (fn [t a]
                                 (let [pax (List. (.head a))
                                       pad (Atom/toByteArray (Atom/jam (.tail a)))]
                                   (save t pax pad)))
                   (noun :url) (fn [t a]
                                 (link t a))}]
  (defn terminal-blit [term blit]
    (let [handler-key (.head blit)
          handler     (handler-map handler-key)]
      (if (nil? h)
        (errln "unhandled terminal blit: " (Noun/toString blit))
        (-> term
            (handler (.tail blit))
            (commit))))))

(defn handle-terminal-effect [state id ovum]
  (let [effect-type (.head ovum)]
    (cond (Noun/equals effect-type (noun :init))
            (let [term (make-lanterna)]
              (send (:event-agent state)
                    (fn [p]
                      (let [pax  [0 :term id]
                            [cols rows] (dimensions term)]
                        (handle! p [:ovum (noun [pax :blew cols rows])])
                        (handle! p [:ovum (noun [pax :hail 0])])
                        p)))
              (assoc-in state [:terminals id] term))
          (Noun/equals effect-type (noun :blit))
            (if (contains? (:terminals state) id)
              (update-in state [:terminals id] #(reduce terminal-blit % (List. (.tail ovum))))
              (errln "bad terminal id: " (Atom/cordToString id)))
          :else
            (errln "unhandled terminal effect: " (Noun/toString effect-type)))))

(defn slog-effect [state tank]
  (let [t (get-in state [:terminals (noun :1)])
        [cols rows] (if (nil? t) [74 24] (dimensions t))
        wall (Tank/wash 0 (long cols) tank)
        lines (map #(Tape/toString %) (List. wall))]
    (if (nil? t)
      (doseq [l lines]
        (errln "slog: " l))
      (commit
        (reduce #(doto %1
                   (line %2)
                   (scroll))
                t lines)))))

(defn handle-effects [state ova]
  (doseq [ovum (List. ova)]
    (let [wire (.head ovum)
          curd (.tail ovum)]
      (if (Atom/isZero (.head wire))
        (if (Atom/equals (noun :term) (.head (.tail wire)))
          (handle-terminal-effect state (.head (.tail (.tail wire))) ovum)
          (unhandled-wire state wire))
        (unhandled-wire state wire)))))

;; machine operations

(defn kernel-axis [m axis]
  (.nock (:context m)
         (get-in m [:persistent :arvo])
         (noun [9 axis 0 1])))

(defn slam [m gate sample]
  (.slam (:context m) gate (noun sample)))

(defn wish [m s]
  (slam m (kernel-axis 20) (Atom/stringToCord s)))

(defn kernel-call [m n s]
  (slam (wish m n) s))

(defn poke [m e o]
  (let [res  (slam (kernel-axis m 42) o)
        eff  (.head res)
        arv  (.tail res)
        newm (assoc-in m [:persistent :arvo] arv)]
    (send e handle-effects eff)
    (set! (. (:context m) caller)
             (reify Caller 
               (slog [this tank]
                 (send e slog-effect tank))
               (kernel [this gate-name sample]
                 (kernel-call new-m gate-name sample))))
    (assoc-in m [:persistent :arvo] arv)))

; boot sequence

(defn set-time [m da]
  (update m :persistent
          #(assoc % :now da :wen (kernel-call m "scot" [:da da]))))

(defn number [m]
  (let [n (Noun/mug (get-in m [:persistent :now]))]
    (update m :persistent
            #(assoc % :sev n :sen (kernel-call m "scot" [:uv n])))))

(defn verbose-on [m e]
  (poke m e [0 :verb 0]))

(defn ames-init [m e]
  (poke m e [[0 :newt (:sen m) 0] :barn 0]))

(defn dill-init [m e]
  (poke m e [[0 :term :1 0] :boot :sith 0 0 0]))

(defn sync-home [m dirpath e]
  (let [f    (io/file dirpath)
        base (Paths/get (.toURI f))]
    (if (or (nil? f)
            (not (.isDirectory f)))
      (do (errln "bad initial sync directory")
          m)
      (let [files (filter #(.isFile %) (file-seq f))
            rels  (map #(.relativize base (Paths/get (.toURI %))) files)
            vis   (filter (fn [path] (not-any? #(.startsWith (.toString %) ".") path)) rels)
            can   (seq->it (map (partial path-to-noun base) vis))
            pax   [0 :sync (:sen m) 0]
            fav   [:into 0 0 can]]
        (poke m e [pax fav])))))

; event handling

(defn add-locations [m context]
  (assoc-in m [:persistent :dashboard] (into {} (.locations context))))

(defn pier-handler [options e]
  (let [jets (read-jets (:jets options))
        ctx  (Context. jets (:profile options))]
    (fn [state [tag data]]
      (:persistent
        (case tag
          :boot (let [sys (read-jam (:solid options))
                      ken (.head sys)
                      roc (.tail sys)
                      cor (.nock ctx 0 ken)] ; "to bind jets"
                  (-> {:persistent {:arvo roc} :context ctx}
                      (set-time)
                      (number)
                      (verbose-on e)
                      (ames-init e)
                      (dill-init e)
                      (sync-home e)
                      (add-locations ctx))))
          :ovum (let [m {:persistent state :context ctx}]
                  (add-locations (poke m e data)))))))

; prevayler
(def initial-pier {})

(defn start-pier [options]
  (let [f (.toFile (Paths/get {:pier options} ".prevayler"))
        e (effect-handler)
        p (prevayler! (pier-handler options e) initial-pier f)
        a (agent p)]
    (send e remember-event-agent a)
    (when (= @p initial-pier)
      (handle! p [:boot nil]))
    a))

(def cli-options
  [["-B" "--solid PATH" "Path to solid pill"]
   ["-J" "--jets PATH" "Path to EDN jet config"]
   ["-A" "--arvo PATH" "Path to initial sync directory"]
   ["-P" "--pier PATH" "Path to pier directory"]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
		(cond (:help options)
        (println summary)
			errors
        (println errors)
      (:solid options)
        (start-pier options))))
