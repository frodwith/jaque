(ns jaque.main
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [clojure.edn :as edn])
  (:require [clojure.tools.cli :as cli])
  (:require [jaque.noun :refer [noun]])
  (:import (net.frodwith.jaque.truffle.driver Arm AxisArm)
           (net.frodwith.jaque
             data.Atom data.Cell data.Noun
             truffle.Context
             truffle.nodes.jet.ImplementationNode))
  (:gen-class))

(defn read-jets [path]
	(let [jets (edn/read (java.io.PushbackReader. (io/reader path)))]
    (into-array Arm
      (map (fn [[label class-name]]
             (let [klass (Class/forName class-name)]
               (if-not (isa? klass ImplementationNode)
                 (throw (Exception. (str "Invalid jet class" class-name)))
                 (AxisArm. label 2 klass))))
           (:gates jets)))))

(defn read-jam [path]
  (let [file (io/as-file path)]
    (with-open [out (java.io.ByteArrayOutputStream.)]
      (io/copy (io/input-stream file) out)
      (-> out (.toByteArray)
              (Atom/fromByteArray Atom/LITTLE_ENDIAN)
              (Atom/cue)))))

(defprotocol Machine
  (nock [m subject formula])
  (slam [m gate sample])
  (call [m gate-name sample])
  (wish [m src]))

(defrecord MachineRec [context kernel-formula kernel-core]
  Machine
  (nock [m subject formula]
    (.nock context subject formula))
  (slam [m gate sample]
    (let [core (noun [(.head gate) sample (.tail (.tail gate))])
          fom  (noun [9 2 0 1])]
      (.nock context core fom)))

    ;(let [fom (noun [9 2 [0 2] [1 sample] 0 7])]
    ;  (println "slam formula" (Noun/toString fom))
    ;  (.nock context gate fom)))
  (call [m gate-name sample]
    (.slam m (.wish m gate-name) sample))
  (wish [m src]
    (let [txt (Atom/stringToCord src)
          gat (.nock context kernel-core (noun [9 20 0 1]))
          pro (.slam m gat txt)]
      (println "txt" (Atom/cordToString txt))
      (print "head of wish gate")
      (Noun/println (.head gat) *out*)
      (print "head of wish product")
      (Noun/println (.head pro) *out*)
      pro)))

(defn boot-pill [pill-path jet-path]
  (let [pill    ^Cell (read-jam pill-path)
        jets    (read-jets jet-path)
        ;ken     (.head pill)
        ken pill ; ivory?
        ; roc     (.tail pill) ; technically, yes.
        ctx     (Context. jets)
        ; roc     (.nock ctx 0 ken) ; but maybe, just maybe
        roc     (.tail (.tail (.nock ctx 0 ken))) ; ivory-boot?
        m       (MachineRec. ctx ken roc)]
    ; (.nock m 0 ken) ; "to register jets". Goes away in slim-boot?
    (println "live: kernel activated")
    (Noun/println (.call m "add" (noun [40 2])))))

(defn boot-formula [jam-path jet-path]
  (let [formula (read-jam jam-path)
        jets    (read-jets jet-path)]
    (prn (.nock (Context. jets) 0 formula))))

(def cli-options
  [["-M" "--formula PATH" "Path to jammed nock formula (nock with 0 subject, print result)"]
   ["-B" "--pill PATH" "Path to urbit boot pill"]
   ["-J" "--jets PATH" "Path to EDN jet config"]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
		(cond (:help options)
        (println summary)
			errors
        (println errors)
      (and (:pill options) (:formula options))
        (println "formula and pill are mutually exclusive")
      (:pill options)
        (boot-pill (:pill options) (:jets options))
      (:formula options)
        (boot-formula (:formula options) (:jets options))
      :else
        (println "specify one of formula or pill"))))
