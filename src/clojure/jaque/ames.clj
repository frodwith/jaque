(ns jaque.ames
  (:use jaque.noun)
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log])
  (:import (net.frodwith.jaque.data Noun Atom Time)
           (java.util Arrays)
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
      (let [i (bit-and 0xff (aget byts 3))
            a (if karl (InetAddress/getLoopbackAddress) (galaxy-dns i))
            p (+ i (if karl 31337 13337))]
        (InetSocketAddress. a p)))))

(defn- lane-help [local pad]
  (let [po (.head pad)
        ad (.tail pad)]
    (if (= 0 ad)
      (InetSocketAddress. (local :host) (local :port))
      (czarify local
        (InetSocketAddress.
          (InetAddress/getByAddress (byte-array (reverse (Atom/forceBytes ad 4))))
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

(defn server-thread [poke ^DatagramSocket udp]
  (Thread.
    (fn []
      (log/info (format "ames: on %s, UDP %d" (.getLocalAddress udp) (.getLocalPort udp)))
      (let [len (.getReceiveBufferSize udp)
            buf (byte-array len)]
        (try
          (loop []
            (let [pack (let [pack (DatagramPacket. buf len)]
                         (locking udp (.receive udp pack))
                         pack)
                  mesg (Atom/fromByteArray (Arrays/copyOfRange (.getData pack) 0 (.getLength pack)))
                  addr (-> pack (.getAddress) (.getAddress) (Atom/fromByteArray Atom/BIG_ENDIAN))
                  port (long (.getPort pack))
                  ovum (noun [[0 :ames 0] :hear [:if (Time/now) port addr] mesg])]
              ;(log/debug "heard" (Noun/toString ovum))
              (async/put! poke ovum)
              (if (Thread/interrupted)
                (throw (IOException.))
                (recur))))
          (catch IOException e
            (log/debug "ames server shutdown")))))))

(defn start [{poke :poke-channel, effects :effect-pub, sen :sen, id :identity,
              galaxy :galaxy, local :local-czars, port :ames-port,
              ^InetAddress host :ames-host}]
  (let [udp  (DatagramSocket. port host)
        loc  {:czars local, :host (.getLocalAddress udp), :port (.getLocalPort udp)}
        sth  (server-thread poke udp)
        paks (async/chan)
        wire (noun [0 :newt sen 0])]
    (async/put! poke (noun [wire :barn 0]))
    (async/sub effects wire paks)
    (.start sth)
    (async/go-loop []
      (let [p (async/<! paks)]
        (if (nil? p)
          (do (log/debug "ames shutdown")
              (.interrupt sth)
              (.close udp))
          (let [data (.tail p)]
            (case (keyword (Atom/cordToString (.head data)))
              :send (let [ld  (.tail data)
                          lan (.head ld)
                          pac (.tail ld)
                          byt (Atom/toByteArray pac)
                          adr (lane->address loc lan)]
                      (if (nil? adr)
                        (log/error "ames: bad lane" (Noun/toString lan))
                        (locking udp (.send udp (DatagramPacket. byt (alength byt) adr)))))
              (log/error "invalid newt"))
            (recur)))))))
