(ns jaque.main
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [clojure.edn :as edn])
  (:require [clojure.tools.cli :as cli])
  (:require [jaque.noun :refer [noun noun? seq->it]])
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:refer-clojure :exclude [time])
  (:import (java.nio.file Paths)
           (java.io File)
           (com.googlecode.lanterna
             screen.Screen
             screen.TerminalScreen
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

(defprotocol DillTerminal
  (spinner-tick [this caption])
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
  (bell [this]
    (.setCharacter screen (.getCursorPosition screen) (TextCharacter. \u0007))
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
          row  (dec (.getRows size))]
      (doseq [[c i] (index-str text)]
        (.setCharacter screen i row (TextCharacter. c)))
      (doseq [i (range (.length text) (.getColumns size))]
        (.setCharacter screen i row (TextCharacter. \space)))
      this))
  (scroll [this]
    (.scrollLines screen 0 (dec (.getRows (.getTerminalSize screen))) 1)
    this)
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
        s (.createScreen f)]
    (.startScreen s)
    (map->Lanterna {:screen s
                    :spinner-state 0})))

(def blits 
  {(noun :bee) #(spinner-tick %1 %2)
   (noun :bel) #(bell %)
   (noun :clr) #(clr %)
   (noun :hop) #(hop %1 (Atom/expectLong %2))
   (noun :lin) #(line %1 (Tape/toString %2))
   (noun :mor) #(scroll %)
   (noun :sav) #(save %1 (List. (.head %2)) (Atom/toByteArray (.tail %2)))
   (noun :sag) #(save %1 (List. (.head %2)) (Atom/toByteArray (Atom/jam (.tail %2))))
   (noun :url) #(link %1 %2)})

(defn blit [term b]
  (let [k (.head b)
        h (blits k)]
    (if (nil? h)
      (binding [*out* *err*]
        (println "bad-blit: " (Noun/toString b))
        term)
      (-> term
          (h (.tail b))
          (commit)))))

(defn -lens [hoon]
  (let [payload  (json/write-str {:source {:dojo hoon}
                                  :sink   {:stdout nil}})
        response (client/post "http://localhost:12321"
                              {:body payload})
        body     (:body response)
        unquot   (.substring body 1 (dec (.length body)))]
    (Noun/parse unquot)))

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

(defprotocol Machine
  (time [m da])
  (install [m new-arvo])
  (numb [m])
  (slam [m gate sample])
  (call [m gate-name sample])
  (arvo-gate [m axis])
  (poke [m ovo])
  (plan [m ovo])
  (wish [m src])
  (nock [m subject formula]))

(defrecord MachineRec [context arvo poke-q term now wen sev sen]
  Machine
  (time [m da]
    (assoc m :now da :wen (call m "scot" [:da da])))
  (install [m new-arvo]
    (let [new-m   (assoc m :arvo new-arvo)
          new-ctx (:context m)]
      (set! (. new-ctx caller)
        (reify Caller 
          (slog [this tank]
            (let [t (:term m)
                  [cols rows] (dimensions t)
                  wall (Tank/wash 0 (long cols) tank)
                  lines (map #(Tape/toString %) (List. wall))]
              (commit
                (reduce #(doto %1
                           (line %2)
                           (scroll))
                        t lines))))
          (kernel [this gate-name sample]
            (call new-m gate-name sample))))
      new-m))
  (numb [m]
    (let [n (Noun/mug now)]
      (assoc m :sev n :sen (call m "scot" [:uv n]))))
  (slam [m gate sample]
    (let [core (noun [(.head gate) sample (.tail (.tail gate))])
          fom  (noun [9 2 0 1])]
      (nock m core fom)))
  (call [m gate-name sample]
    (slam m (wish m gate-name) sample))
  (arvo-gate [m axis]
    (nock m arvo (noun [9 axis 0 1])))
  (plan [m ovo]
    (assoc m :poke-q (conj poke-q ovo)))
  (poke [m ovo]
    (slam m (arvo-gate m 42) (noun [now ovo])))
  (wish [m src]
    (slam m (arvo-gate m 20) (Atom/stringToCord src)))
  (nock [m subject formula]
    (.nock context subject formula)))

(defn ames-init [m]
  (plan m (noun [[0 :newt (:sen m) 0] :barn 0])))

(defn dill-init [m]
  (let [pax [0 :term :1 0]
        [cols rows] (dimensions (:term m))
        lan #(plan %1 (noun [pax %2]))]
    (-> m 
        (lan [:boot :sith 0 0 0]) ; only fakezod for now
        (lan [:blew cols rows])
        (lan [:hail 0]))))

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

(defn sync-home [m dirpath]
  (let [f    (io/file dirpath)
        base (Paths/get (.toURI f))]
    (if (or (nil? f)
            (not (.isDirectory f)))
      (do (println "bad initial sync directory")
          m)
      (let [files (filter #(.isFile %) (file-seq f))
            rels  (map #(.relativize base (Paths/get (.toURI %))) files)
            vis   (filter (fn [path] (not-any? #(.startsWith (.toString %) ".") path)) rels)
            can   (seq->it (map (partial path-to-noun base) vis))
            pax   [0 :sync (:sen m) 0]
            fav   [:into 0 0 can]]
        (plan m (noun [pax fav]))))))

(defn boot [ctx roc]
  (let [m (-> (map->MachineRec {:context ctx 
                                :term    (make-lanterna)
                                :poke-q  clojure.lang.PersistentQueue/EMPTY})
              (install roc)
              (time (Time/now))
              (numb))]
    (println "arvo time: " (Atom/cordToString (:wen m)))
    m))

(defn boot-ivory [pill-path jet-path]
  (let [jets (read-jets jet-path)
        ken  (read-jam pill-path)
        ctx  (Context. jets)
        roc  (.tail (.tail (.nock ctx 0 ken)))
        m    (boot ctx roc)]
    (println "ivory: kernel activated")
    (Noun/println (call m "add" (noun [40 2])) *out*)))

(defn apply-effect [m effect]
  (if (Noun/equals (.head effect) (noun [0 :term 49 0]))
    (let [ect (.tail effect)]
      (if (Noun/equals (.head ect) (noun :blit))
        (assoc m :term (reduce blit (:term m) (List. (.tail ect))))
        (binding [*out* *err*]
          (println "bad-term: " (Noun/toString ect))))
      m))
  (print "effect: ")
  (Noun/println effect *out*)
  m)

(defn apply-poke [m ovo]
  (let [result  (poke m ovo)
        effects (.head result)
        arvo    (.tail result)]
    (print "poked: ")
    (Noun/println (.head (.tail ovo)) *out*)
    (install (reduce apply-effect m (List. effects))
             arvo)))

(defn work [m]
  (assoc (reduce apply-poke m (:poke-q m))
         :poke-q nil))

(defn boot-solid [pill-path jet-path arvo-path]
  (let [jets (read-jets jet-path)
        sys  (read-jam pill-path)
        ctx  (Context. jets)
        ken  (.head sys)
        roc  (.tail sys)
        cor  (let [c (.nock ctx 0 ken)] ; "to bind jets"
               (println "solid: kernel activated")
               c)
        m    (-> (boot ctx roc)
                 (ames-init)
                 (dill-init)
                 (plan (noun [0 :verb 0]))
                 (sync-home arvo-path)
                 (work))]
    (.dumpProfile ctx)
    (Noun/println (call m "add" [40 2]) *out*)))

(defn boot-formula [jam-path jet-path]
  (let [formula (read-jam jam-path)
        jets    (read-jets jet-path)]
    (Noun/println (.nock (Context. jets) 0 formula) *out*)))

(def cli-options
  [["-M" "--formula PATH" "Path to jammed nock formula (nock with 0 subject, print result)"]
   ["-I" "--ivory PATH" "Path to ivory pill"]
   ["-B" "--solid PATH" "Path to solid pill"]
   ["-J" "--jets PATH" "Path to EDN jet config"]
   ["-A" "--arvo PATH" "Path to initial sync directory"]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
		(cond (:help options)
        (println summary)
			errors
        (println errors)
      (not= 1 (count (filter #(contains? options %) [:ivory :solid :formula])))
        (println "Pick one of [formula,solid,ivory]")
      (:ivory options)
        (boot-ivory (:ivory options) (:jets options))
      (:solid options)
        (boot-solid (:solid options) (:jets options) (:arvo options))
      (:formula options)
        (boot-formula (:formula options) (:jets options)))))
