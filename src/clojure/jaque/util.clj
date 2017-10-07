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
                    (throw (Exception. (str "Invalid jet class" class-name))))))]
    (into-array Arm
      ((fn collect-from-core [pfx cor]
         (let [[n a & c] cor
               fix (conj pfx n)
               label (string/join "/" (map name fix))
               arms (if (string? a)
                      [(AxisArm. label 2 (klass a))]
                      (map (fn [[k v]]
                               (if (keyword? k)
                                 (NamedArm. label (name k) (klass v))
                                 (AxisArm. label k (klass v))))
                           a))]
           (apply (partial concat arms)
                  (map (partial collect-from-core fix) c))))
       [] jets))))

(defn atom-from-file [file]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (io/copy (io/input-stream file) out)
    (-> out (.toByteArray)
        (Atom/fromByteArray Atom/LITTLE_ENDIAN))))

(defn read-jam [file]
  (Atom/cue (atom-from-file file)))

(defn path-seq-to-file [base pas]
  (let [end  (case (count pas)
               0 nil
               1 pas
               (let [[nam ext] (take-last 2 pas)]
                 (concat (drop-last 2 pas) 
                         [(format "%s.%s" nam ext)])))
        path (apply (partial pier-path base) end)]
    (.toFile path)))

(defn write-file [file byts]
  (.mkdirs (.getParentFile file))
  (with-open [out (io/output-stream file)]
    (.write out byts)))

(defn path-to-noun [base path]
  (let [fpat  (.resolve base path)
        file  (.toFile fpat)
        fnam  (.getName file)
        dot   (string/last-index-of fnam \.)
        bef   (subs fnam 0 dot)
        aft   (subs fnam (inc dot))
        namp  (remove string/blank? [bef aft])
        parp  (.getParent fpat)
        full  (if (= 0 (.compareTo base parp))
                namp
                (concat (.relativize base (.getParent fpat))
                        namp))
        cords (map #(Atom/stringToCord (.toString %)) full)
        pax   (seq->it cords)
        dat   (if (.exists file)
                (let [mime [:text :plain 0]
                      size (.length file)
                      contents (atom-from-file file)]
                  [0 mime size contents])
                0)]
    (noun [pax dat])))

(defn dir-can [dir]
  (let [base (Paths/get (.toURI dir))]
    (let [files (filter #(.isFile %) (file-seq dir))
          rels  (map #(.relativize base (Paths/get (.toURI %))) files)
          vis   (filter (fn [path] (not-any? #(.startsWith (.toString %) ".") path)) rels)]
      (seq->it (map (partial path-to-noun base) vis)))))
