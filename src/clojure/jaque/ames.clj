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

(defn- czarify [fake addr]
  (let [inet (.getAddress addr)
        byts (.getAddress inet)]
    (if-not (and (= 0 (aget byts 0))
                 (= 0 (aget byts 1))
                 (= 1 (aget byts 2)))
      addr
      (let [i (aget byts 3)
            a (if fake (InetAddress/getLocalHost) (galaxy-dns i))
            p (+ i (if fake 31337 13337))]
        (InetSocketAddress. a p)))))

(defn- lane-help [fake local-port pad]
  (let [ad (.head pad)
        po (.tail pad)]
    (if (= 0 ad)
      (InetSocketAddress. (InetAddress/getLocalHost) local-port)
      (czarify fake
        (InetSocketAddress.
          (InetAddress/getByAddress (Atom/toByteArray ad))
          (Atom/expectLong po))))))

(defn- lane->address [fake local-port lane]
  (loop [lane lane]
    (let [pad (.tail (.tail lane))]
      (case (keyword (Atom/cordToString (.head lane)))
        :if (lane-help fake local-port pad)
        :is (let [pq (.head pad)]
              (if (= pq 0)
                nil
                (recur (.tail pq))))
        :ix (lane-help local-port pad)
        nil))))

(defn server-thread [poke host port]
  (Thread.
    #(let [srv (DatagramSocket. host port)]
       (log/info (format "ames: on %s, UDP %d" host port))
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
              fake :fake, port :port, ^InetAddress host :host-address}]
  (let [sth  (server-thread poke host port)
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
                          adr (lane->address fake port lan)]
                      (if (nil? adr)
                        (log/error "ames: bad lane" (Noun/toString lan))
                        (doto (DatagramSocket.)
                          (.connect adr)
                          (.send (DatagramPacket. byt (alength byt)))
                          (.close))))
              (log/error "invalid newt"))
            (recur)))))))
