(ns jaque.main
  (:require [clojure.java.io :as io]
            [clojure.core.async :refer [chan pub <!!]]
            [clojure.tools.cli :as cli]
            [jaque.terminal :as term]
            [jaque.fs :as fs]
            [jaque.util :as util]
            [jaque.http :as http]
            [jaque.noun :refer [noun]]
            [jaque.kernel :as kern])
  (:import (org.httpkit.server AsyncChannel) ; to make the loader for main AOT happy
           (java.net ServerSocket)
           (java.io IOException)
           (net.frodwith.jaque.data Cell))
  (:gen-class))

(defn- by-wire [^Cell ovum]
  (.head ovum))

(defn run [options]
  (let [poke    (chan) ; shutdown by terminal (logo)
        tank    (chan) ; shutdown by terminal (logo)
        eff     (chan) ; shutdown by kernel
        effects (pub eff by-wire)
        home    (:home options)
        [kinit kth]
          (kern/start 
            {:profile        (contains? options :profile)
             :jet-path       (:jets options)
             :pill-path      (:pill options)
             :sync-path      (:arvo options)
             :pier-path      home
             :effect-channel eff
             :tank-channel   tank
             :poke-channel   poke})
        term-ch (term/start 
                  {:effect-pub     effects
                   :terminal-id    (noun :1)
                   :tank-channel   tank
                   :poke-channel   poke
                   :kernel-thread  kth
                   :save-root      (util/pier-path home ".urb" "put")})
        sen     (kinit)
        fs-ch   (fs/start
                  {:effect-pub     effects
                   :poke-channel   poke
                   :mount-dir      home
                   :sen            sen})
        stop-http (http/start
                    {:effect-pub     effects
                     :poke-channel   poke
                     :port           (:eyre-port options)})]
    (.start kth)
    (<!! term-ch)
    (<!! fs-ch)
    (.join kth)
    (stop-http)))

(defn exists? [is-dir path]
  (let [f (io/as-file path)]
    (and (.exists f)
         (or (and is-dir (.isDirectory f))
             (and (not is-dir) (.isFile f))))))

(def directory-exists? (partial exists? true))
(def file-exists? (partial exists? false))

(defn tcp-port-open? [port]
  (let [ss (try (doto (ServerSocket. port)
                      (.setReuseAddress true))
             (catch IOException e
               nil))]
    (if (nil? ss)
      false
      (do (.close ss)
          true))))

(def cli-options
  [["-P" "--profile"   "Enable profiling dump"]
   ["-B" "--pill PATH" "Path to solid pill"
    :parse-fn io/as-file
    :validate [#(and (.exists %) (.isFile %)) "pill not found"]]
   ["-J" "--jets PATH" "Path to EDN jet config"
    :parse-fn io/as-file
    :validate [#(and (.exists %) (.isFile %)) "jet config not found"]]
   ["-A" "--arvo PATH" "Path to initial sync directory"
    :parse-fn io/as-file
    :validate [#(and (.exists %) (.isDirectory %)) "initial sync not found"]]
   ["-E" "--eyre-port PORTNUM" "port number for eyre http server"
    :default 8080
    :parse-fn #(Integer/parseInt %)
    :validate [tcp-port-open? "eyre port not available"]]
   ["-H" "--home PATH" "Path to pier home directory"
    :parse-fn io/as-file
    :validate [#(or (not (.exists %))
                    (and (.exists %) (.isDirectory %)))
               "invalid pier directory"]]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
		(cond (:help options)
        (println summary)
			errors
        (println errors)
      :else
        (run options))))
