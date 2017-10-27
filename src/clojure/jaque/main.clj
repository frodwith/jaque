(ns jaque.main
  (:require [clojure.java.io :as io]
            [clojure.core.async :refer [chan pub <!!]]
            [clojure.tools.cli :as cli]
            [clojure.string :as string]
            [jaque.terminal :as term]
            [jaque.fs :as fs]
            [jaque.ames :as ames]
            [jaque.util :as util]
            [jaque.http :as http]
            [jaque.noun :refer [noun]]
            [jaque.kernel :as kern])
  (:import (org.httpkit.server AsyncChannel) ; to make the loader for main AOT happy
           (java.net ServerSocket InetAddress)
           (java.io IOException)
           (net.frodwith.jaque truffle.driver.Arm truffle.Context data.Noun data.Cell))
  (:gen-class))

(defn- by-wire [^Cell ovum]
  (.head ovum))

(defn run [options]
  (let [poke    (chan) ; shutdown by terminal (logo)
        tank    (chan) ; shutdown by terminal (logo)
        eff     (chan) ; shutdown by kernel
        effects (pub eff by-wire)
        home    (:home options)
        fake    (:fake options)
        local   (:local-czars options)
        [kinit kth]
          (kern/start 
            {:profile        (contains? options :profile)
             :jet-path       (:jets options)
             :pill-path      (:pill options)
             :sync-path      (:arvo options)
             :identity       (:identity options)
             :ticket         (:ticket options)
             :fake           fake
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
        [sen who galaxy] (kinit)
        fs-ch   (fs/start
                  {:effect-pub     effects
                   :poke-channel   poke
                   :mount-dir      home
                   :sen            sen})
        ames-ch (ames/start
                  {:effect-pub     effects
                   :poke-channel   poke
                   :sen            sen
                   :identity       who
                   :galaxy         galaxy
                   :local-czars    local
                   :ames-port      (if galaxy
                                     (+ who (if local 31337 13337))
                                     (:ames-port options))
                   :ames-host      (if local
                                     (InetAddress/getLoopbackAddress)
                                     (:ames-host options))})
        stop-http (http/start
                    {:effect-pub     effects
                     :poke-channel   poke
                     :sen            sen
                     :port           (:eyre-port options)})]
    (.start kth)
    (<!! term-ch)
    (<!! fs-ch)
    (<!! ames-ch)
    (.join kth)
    (stop-http)))

(defn run-formula-pill [options]
  (let [arms (if (:jets options)
               (util/read-jets (:jets options))
               (make-array Arm 0))
        ctx  (let [ctx (Context.)]
               (.wake ctx arms nil (boolean (:profile options)))
               ctx)
        res  (.bloc ctx 0 (util/read-jam (:formula options)))]
    (println (Noun/toString res))))

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

(defn- add-sig [s]
  (if (string/starts-with? s "~")
    s
    (str "~" s)))

(def cli-options
  [["-P" "--profile"   "Enable profiling dump"]
   ["-F" "--fake" "use null security (implies -L)"]
   ["-L" "--local-czars" "talk to galaxies only on localhost"]
   ["-I" "--identity SHIPNAME" "Ship name (e.g. samzod-dozzod)"
    :parse-fn add-sig]
   ["-t" "--ticket TICKET" "Bootup crypto secret (e.g. samzod-dozzod-samzod-dozzod)"
    :parse-fn add-sig]
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
   ["-p" "--ames-port PORT" "local udp port for ames to listen on"
    :default 0
    :parse-fn #(Integer/parseInt %)]
   ["-l" "--ames-host HOST" "local host for ames to listen on"
    :default (InetAddress/getByName "0.0.0.0")
    :parse-fn #(InetAddress/getByName %)]
   ["-H" "--home PATH" "Path to pier home directory"
    :parse-fn io/as-file
    :validate [#(or (not (.exists %))
                    (and (.exists %) (.isDirectory %)))
               "invalid pier directory"]]
   ["-N" "--formula PATH" "Nock the formula pill file at PATH with ~ subject, print result, and exit."
    :parse-fn io/as-file]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
		(cond 
      (:help options)
        (println summary)
			errors
        (println errors)
      (:formula options)
        (run-formula-pill options)
      :else (let [o (if (:fake options) (assoc options :local-czars true) options)]
              (run o)))))
