(ns jaque.main
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [clojure.edn :as edn])
  (:require [clojure.tools.cli :as cli])
  (:require [jaque.noun :refer [noun]])
  (:import (net.frodwith.jaque.truffle.driver Arm NamedArm AxisArm)
           (net.frodwith.jaque
             data.Atom data.Cell data.Noun
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
                             :else (throw (Exception. "Invalid arm type" (str typ))))))
                   (:arms jets))]
    (into-array Arm (concat arms gates))))

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
  (call [m gate-name sample]
    (.slam m (.wish m gate-name) sample))
  (wish [m src]
    (let [txt (Atom/stringToCord src)
          gat (.nock context kernel-core (noun [9 20 0 1]))
          pro (.slam m gat txt)]
      pro)))

(defn boot-pill [pill-path jet-path]
  (let [jets    (read-jets jet-path)
        ken     (read-jam pill-path)
        ctx     (Context. jets)
        roc     (.tail (.tail (.nock ctx 0 ken)))
        m       (MachineRec. ctx ken roc)]
    (println "live: kernel activated")
    (Noun/println (call m "add" (noun [40 2])) *out*)))

(defn boot-formula [jam-path jet-path]
  (let [formula (read-jam jam-path)
        jets    (read-jets jet-path)]
    (Noun/println (.nock (Context. jets) 0 formula) *out*)))

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
