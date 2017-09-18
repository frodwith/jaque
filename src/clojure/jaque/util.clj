(ns jaque.util
  (:use jaque.noun)
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
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

(defn write-file [base path-seq byts]
  (let [end  (case (count path-seq)
               0 nil
               1 path-seq
               (let [[nam ext] (take-last 2 path-seq)]
                 (concat (drop-last 2 path-seq) 
                         [(format "%s.%s" nam ext)])))
        path (apply (partial pier-path base) end)
        file (.toFile path)]
    (.mkdirs (.getParentFile file))
    (with-open [out (io/output-stream file)]
      (.write out byts))))

(defn- atom-from-file [file]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (io/copy (io/input-stream file) out)
    (-> out (.toByteArray)
        (Atom/fromByteArray Atom/LITTLE_ENDIAN))))

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
