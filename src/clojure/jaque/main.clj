(ns jaque.main
  (:require [clojure.java.io :as io]
            [clojure.core.async :refer [chan pub <!!]]
            [clojure.tools.cli :as cli]
            [jaque.terminal :as term]
            [jaque.noun :refer [noun]]
            [jaque.kernel :as kern])
  (:import (java.nio.file Paths)
           (net.frodwith.jaque.data Cell))
  (:gen-class))

(defn- by-wire [^Cell ovum]
  (.head ovum))

(defn run [options]
  (let [poke    (chan) ; shutdown by terminal (logo)
        tank    (chan) ; shutdown by terminal (logo)
        ;call   (chan)
        eff     (chan) ; shutdown by kernel
        ;http   (chan)
        effects (pub eff by-wire)
        home    (:home options)
        putdir  (Paths/get home (into-array [".urb" "put"]))
        term-ch (term/start 
                  {:effect-channel effects
                   :terminal-id    (noun :1)
                   :tank-channel   tank
                   :poke-channel   poke
                   :save-root      putdir})
        kern-ch (kern/start 
                  {:profile        (contains? options :profile)
                   :jet-path       (:jets options)
                   :pill-path      (:pill options)
                   :sync-path      (:arvo options)
                   :pier-path      home
                   :effect-channel eff
                   :tank-channel   tank
                   ;:call-channel   call
                   :poke-channel   poke})]
    (<!! term-ch)
    (<!! kern-ch)))

(defn exists? [is-dir path]
  (let [f (io/as-file path)]
    (and (.exists f)
         (or (and is-dir (.isDirectory f))
             (and (not is-dir) (.isFile f))))))

(def directory-exists? (partial exists? true))
(def file-exists? (partial exists? false))

(def cli-options
  [["-B" "--pill PATH" "Path to solid pill" :validate [file-exists?]]
   ["-J" "--jets PATH" "Path to EDN jet config" :validate [file-exists?]]
   ["-A" "--arvo PATH" "Path to initial sync directory" :validate [directory-exists?]]
   ["-H" "--home PATH" "Path to pier home directory" :validate [directory-exists?]]
   ["-P" "--profile"   "Enable profiling dump"]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
		(cond (:help options)
        (println summary)
			errors
        (println errors)
      :else
        (run options))))
