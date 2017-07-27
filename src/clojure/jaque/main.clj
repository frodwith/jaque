(ns jaque.main
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [clojure.edn :as edn])
  (:require [clojure.tools.cli :as cli])
  (:require [jaque.noun :refer [noun seq->it]])
  (:import (java.nio.file Paths)
           (net.frodwith.jaque.truffle.driver Arm NamedArm AxisArm)
           (net.frodwith.jaque
             data.Atom
             data.Cell
             data.Noun
             data.Time
             truffle.Context
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
  (nock [m subject formula])
  (slam [m gate sample])
  (call [m gate-name sample])
  (arvo-gate [m axis])
  (poke [m ovo])
  (wish [m src]))

(defrecord MachineRec [context now kernel-formula kernel-core]
  Machine
  (nock [m subject formula]
    (.nock context subject formula))
  (slam [m gate sample]
    (let [core (noun [(.head gate) sample (.tail (.tail gate))])
          fom  (noun [9 2 0 1])]
      (nock m core fom)))
  (call [m gate-name sample]
    (slam m (wish m gate-name) sample))
  (arvo-gate [m axis]
    (nock m kernel-core (noun [9 axis 0 1])))
  (poke [m ovo]
    (slam m (arvo-gate m 42) (noun [now ovo])))
  (wish [m src]
    (slam m (arvo-gate m 20) (Atom/stringToCord src))))

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
    (Noun/println pax *out*)
    (noun [pax 0 dat])))

(defn unhidden [path]
  (not-any? #(.startsWith (.toString %) ".") path))

(defn dir-events [dirpath]
  (let [f    (io/file dirpath)
        base (Paths/get (.toURI f))]
    (if (or (nil? f)
            (not (.isDirectory f)))
      []
      (map (partial path-to-noun base)
           (filter unhidden
                   (map #(.relativize base (Paths/get (.toURI %)))
                        (filter #(.isFile %) (file-seq f))))))))

(defn boot-ivory [pill-path jet-path]
  (let [jets (read-jets jet-path)
        ken  (read-jam pill-path)
        ctx  (Context. jets)
        roc  (.tail (.tail (.nock ctx 0 ken)))
        m    (MachineRec. ctx (Time/now) ken roc)]
    (println "ivory: kernel activated")
    (Noun/println (call m "add" (noun [40 2])) *out*)))

(defn boot-solid [pill-path jet-path arvo-path]
  (let [jets (read-jets jet-path)
        sys  (read-jam pill-path)
        ctx  (Context. jets)
        ken  (.head sys)
        roc  (.tail sys)
        cor  (.nock ctx 0 ken) ; "to bind jets"
        m    (MachineRec. ctx (Time/now) ken roc)
        init (dir-events arvo-path)
        can  (seq->it init)
        sev  (Noun/mug (:now m))
        sen  (call m "scot" [:uv sev])
        pax  [0 :sync sen 0]
        fav  [:into 0 0 can]
        egg  (noun [pax fav])]
    (println "solid: kernel activated")
    ;; i think we're supposed to send clay %init before we send all this
    ;; %into -- in general, let's start trying to emulate the normal boot
    ;; sequence and not get ahead of ourselves...
    (poke m egg)
    (println "i think we poked it jim")))

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
