(ns jaque.main
  (:require [clojure.java.io :as io])
  (:require [clojure.edn :as edn])
  (:require [clojure.tools.cli :as cli])
  (:import (net.frodwith.jaque.truffle.driver Arm AxisArm)
           (net.frodwith.jaque
             data.Atom
             truffle.Context
             truffle.nodes.jet.ImplementationNode))
  (:gen-class))

(def opts
  [["-B" "--pill PATH" "Path to boot pill" :default "urbit.pill"]
   ["-J" "--jets PATH" "Path to EDN jet config" :default "jets.edn"]])

(defn -main [& args]
  (let [parsed (:options (cli/parse-opts args opts))
        pill-file (io/as-file (:pill parsed))
        pill-bytes (with-open [out (java.io.ByteArrayOutputStream.)]
                     (io/copy (io/input-stream pill-file) out)
                     (.toByteArray out))
        pill-atom (Atom/fromByteArray pill-bytes Atom/LITTLE_ENDIAN)
        kernel-formula (Atom/cue pill-atom)
        jets (edn/read (java.io.PushbackReader. (io/reader (:jets parsed))))
        arms (map (fn [[label class-name]]
                    (let [klass (Class/forName class-name)]
                      (if-not (isa? klass ImplementationNode)
                        (throw (Exception. (str "Invalid jet class" class-name)))
                        (AxisArm. label 2 klass))))
                  (:gates jets))
        context (Context. (into-array Arm arms))]
    (prn (.nock context 0 kernel-formula))))
