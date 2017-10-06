(ns jaque.ames
  (:use jaque.noun)
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log])
  (:import (net.frodwith.jaque.data Noun Atom Time)
           (java.time Instant)
           (java.io IOException)
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
            a (if karl (InetAddress/getLoopbackAddress) (galaxy-dns i))
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
      (log/info (format "ames: on %s, UDP %d" (.getLocalAddress srv) (.getLocalPort srv)))
      (try
        (loop []
          (let [pack (let [buf (byte-array 1400) ; 1400 is a google-educated guess for max udp packet size
                           p (DatagramPacket. buf 1400)]
                       (.receive srv p)
                       p)
                mesg (-> pack (.getData) (Atom/fromByteArray))
                addr (-> pack (.getAddress) (.getAddress) (Atom/fromByteArray))
                port (long (.getPort pack))
                wire [0 :ames 0]
                ovum (noun [[0 :ames 0] :hear [:if (Time/now) port addr] mesg])]
            (async/put! poke ovum)
            (if (Thread/interrupted)
              (throw (IOException.))
              (recur))))
        (catch IOException e
          (log/debug "ames server shutdown"))))))

(defn start [{poke :poke-channel, effects :effect-pub, sen :sen, id :identity,
              galaxy :galaxy, local :local-czars, port :ames-port,
              ^InetAddress host :ames-host}]
  (let [srv  (DatagramSocket. port host)
        loc  {:czars local, :host (.getLocalAddress srv), :port (.getLocalPort srv)}
        sth  (server-thread poke srv)
        paks (async/chan)
        wire [0 :newt sen 0]]
    (async/put! poke (noun [wire :barn 0]))
    (async/sub effects wire paks)
    (.start sth)
    (async/go-loop []
      (let [p (async/<! paks)]
        (if (nil? p)
          (do (log/debug "ames shutdown")
              (.interrupt sth)
              (.close srv))
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
                          (.send (DatagramPacket. byt (alength byt) adr))
                          (.close))))
              (log/error "invalid newt"))
            (recur)))))))
