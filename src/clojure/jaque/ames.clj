(ns jaque.ames
  (:use jaque.noun)
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log])
  (:import (net.frodwith.jaque.data Noun Atom Time)
           (java.time Instant)
           (java.net InetAddress InetSocketAddress DatagramSocket DatagramPacket)))

(def galaxy-names (into [] (map #(apply str %) (partition 3 "zodnecbudwessevpersutletfulpensytdurwepserwylsunrypsyxdyrnuphebpeglupdepdysputlughecryttyvsydnexlunmeplutseppesdelsulpedtemledtulmetwenbynhexfebpyldulhetmevruttylwydtepbesdexsefwycburderneppurrysrebdennutsubpetrulsynregtydsupsemwynrecmegnetsecmulnymtevwebsummutnyxrextebfushepbenmuswyxsymselrucdecwexsyrwetdylmynmesdetbetbeltuxtugmyrpelsyptermebsetdutdegtexsurfeltudnuxruxrenwytnubmedlytdusnebrumtynseglyxpunresredfunrevrefmectedrusbexlebduxrynnumpyxrygryxfeptyrtustyclegnemfermertenlusnussyltecmexpubrymtucfyllepdebbermughuttunbylsudpemdevlurdefbusbeprunmelpexdytbyttyplevmylwedducfurfexnulluclennerlexrupnedlecrydlydfenwelnydhusrelrudneshesfetdesretdunlernyrsebhulrylludremlysfynwerrycsugnysnyllyndyndemluxfedsedbecmunlyrtesmudnytbyrsenwegfyrmurtelreptegpecnelnevfes"))))

(def galaxy-cache (atom (vec (replicate 256 nil))))

(defn- refresh-galaxy-dns! [imp]
  (let [n (galaxy-names imp)
        d (format "%s.urbit.org" n)
        a (InetAddress/getByName d)]
    (log/debug (format "resolved ~%s to %s" n a))
    (swap! galaxy-cache (fn [old] (assoc old imp {:address a, :expires (.plusMillis (Instant/now) 300)})))
    a))

(defn galaxy-dns [imp]
  (let [old (@galaxy-cache imp)]
    (if (nil? old)
      (refresh-galaxy-dns! imp)
      (if (= 1 (.compareTo (:expires old) (Instant/now)))
        (refresh-galaxy-dns! imp)
        (:address old)))))

(defn- czarify [local addr]
  (let [inet (.getAddress addr)
        karl (local :czars)
        byts (.getAddress inet)]
    (if-not (and (= 0 (aget byts 0))
                 (= 0 (aget byts 1))
                 (= 1 (aget byts 2)))
      addr
      (let [i (aget byts 3)
            a (if karl (InetAddress/getLocalHost) (galaxy-dns i))
            p (+ i (if karl 31337 13337))]
        (InetSocketAddress. a p)))))

(defn- lane-help [local pad]
  (let [ad (.head pad)
        po (.tail pad)]
    (if (= 0 ad)
      (InetSocketAddress. (local :host) (local :port))
      (czarify local
        (InetSocketAddress.
          (InetAddress/getByAddress (Atom/toByteArray ad))
          (Atom/expectLong po))))))

(defn- lane->address [local lane]
  (loop [lane lane]
    (let [pad (.tail (.tail lane))]
      (case (keyword (Atom/cordToString (.head lane)))
        :if (lane-help local pad)
        :is (let [pq (.head pad)]
              (if (= pq 0)
                nil
                (recur (.tail pq))))
        :ix (lane-help local pad)
        nil))))

(defn server-thread [poke ^DatagramSocket srv]
  (Thread.
    (fn []
      (log/info (format "ames: on %s, UDP %d" (.getInetAdress srv) (.getPort srv)))
      (try
        (loop []
          (let [pack (.recieve srv)
                mesg (-> pack (.getData) (Atom/fromByteArray))
                addr (-> pack (.getAddress) (.getAddress) (Atom/fromByteArray))
                port (long (.getPort pack))
                opac (async/chan)
                wire [0 :ames 0]
                ovum [[0 :ames 0] :hear [:if (Time/now) port addr] mesg]]
            (async/put! poke ovum)
            (if (Thread/interrupted)
              (throw (InterruptedException.))
              (recur))))
        (catch InterruptedException e
          (log/debug "ames server shutdown"))))))

(defn start [{poke :poke-channel, effects :effect-pub, sen :sen, 
              local :localhost-czars, port :ames-port, ^InetAddress host :ames-host}]
  (let [srv  (DatagramSocket. host port)
        loc  {:czars local, :host (.getInetAddress srv), :port (.getPort srv)}
        sth  (server-thread poke srv)
        paks (async/chan)
        wire [0 :newt sen 0]]
    (async/put! (noun [wire :barn 0]))
    (async/sub effects wire paks)
    (.start sth)
    (async/go-loop []
      (let [p (async/<! paks)]
        (if (nil? p)
          (do (log/debug "ames shutdown")
              (.interrupt sth))
          (let [data (.tail p)]
            (case (keyword (Atom/cordToString (.head data)))
              :send (let [ld  (.tail data)
                          lan (.head ld)
                          pac (.tail ld)
                          byt (Atom/toByteArray pac)
                          adr (lane->address loc lan)]
                      (if (nil? adr)
                        (log/error "ames: bad lane" (Noun/toString lan))
                        (doto (DatagramSocket.)
                          (.connect adr)
                          (.send (DatagramPacket. byt (alength byt)))
                          (.close))))
              (log/error "invalid newt"))
            (recur)))))))
