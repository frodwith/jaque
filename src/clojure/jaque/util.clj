(ns jaque.util
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import 
    (java.nio.file Paths)
    (net.frodwith.jaque
      data.Atom
      truffle.driver.Arm
      truffle.driver.NamedArm
      truffle.driver.AxisArm
      truffle.nodes.jet.ImplementationNode)))

(defn pier-path [base & parts]
  (Paths/get (str base) (into-array String parts)))

(defn read-jets [file]
	(let [jets  (edn/read (java.io.PushbackReader. (io/reader file)))
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

(defn read-jam [file]
  (Atom/cue (atom-from-file file)))
