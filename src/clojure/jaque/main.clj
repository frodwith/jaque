(ns jaque.main
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [clojure.edn :as edn])
  (:require [clojure.tools.cli :as cli])
  (:require [jaque.noun :refer [noun seq->it]])
  (:refer-clojure :exclude [time])
  (:import (java.nio.file Paths)
           (net.frodwith.jaque.truffle.driver Arm NamedArm AxisArm)
           (net.frodwith.jaque
             data.Atom
             data.Cell
             data.Noun
             data.List
             data.Time
             truffle.Context
             truffle.Caller
             truffle.nodes.jet.ImplementationNode))
  (:gen-class))

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
  (numb [m])
  (slam [m gate sample])
  (call [m gate-name sample])
  (arvo-gate [m axis])
  (poke [m ovo])
  (plan [m ovo])
  (wish [m src])
  (nock [m subject formula]))

(defrecord MachineRec [context arvo poke-q now wen sev sen]
  Machine
  (time [m da]
    (assoc m :now da :wen (call m "scot" [:da da])))
  (numb [m]
    (let [n (Noun/mug now)]
      (assoc m :sev sev :sen (call m "scot" [:uv n]))))
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
    (set! (. context caller) 
          (reify Caller 
            (kernel [this gate-name sample]
              (call m gate-name sample))))
    (.nock context subject formula)))

(defn ames-init [m]
  (plan m (noun [[0 :newt (:sen m) 0] :barn 0])))

(defn dill-init [m]
  (let [pax [0 :term :1 0]
        lan #(plan %1 (noun [pax %2]))]
    (-> m 
        (lan [:boot :sith 0 0 0]) ; only fakezod for now
        (lan [:blew 80 24])       ; only real terminals
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
                                :arvo    roc
                                :poke-q  clojure.lang.PersistentQueue/EMPTY})
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
  (print "effect: ")
  (Noun/println effect *out*)
  m)

(defn apply-poke [m ovo]
  (let [result  (poke m ovo)
        effects (.head result)
        arvo    (.tail result)]
    (print "poked: ")
    (Noun/println (.head (.tail ovo)) *out*)
    (assoc (reduce apply-effect m (List. effects))
           :arvo arvo)))

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
                 (sync-home arvo-path)
                 (work))]
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
