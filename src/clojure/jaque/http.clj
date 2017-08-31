(ns jaque.http
  (:use [jaque.noun])
  (:require [clojure.core.async :refer [<! <!! >! go go-loop alt! chan]]
            [clojure.tools.logging :as log]
            [clojure.string :as string]
            [ring.adapter.jetty :as jetty])
  (:import 
    (java.io ByteArrayInputStream)
    (net.frodwith.jaque.data Atom Noun List)))

(defn capitalize [low]
  (-> low 
      (string/replace #"^." string/upper-case)  
      (string/replace #"-." string/upper-case)))

(defn req-header-to-noun [[lowname value]]
  (noun [(Atom/stringToCord (capitalize lowname)) (Atom/stringToCord value)]))

(defn ip-string-to-atom [s]
  (let [octs (string/split s #"\.")
        by   (into-array Byte/TYPE (map #(Byte/parseByte %) octs))]
    (Atom/fromByteArray by)))

(defn request-to-poke [id req]
  (let [m (:request-method req)]
    (if-not (contains? #{:head :get :put :post} m)
      (do (log/warn "strange request: " m)
          nil)
      (let [med (noun m)
            url (Atom/stringToCord
                  (let [front (:uri req)
                        query (:query-string req)]
                    (if (nil? query)
                      front
                      (str front "?" query))))
            hed (seq->it (map req-header-to-noun (:headers req)))
            bod (let [cord (Atom/stringToCord (slurp (:body req)))]
                  (if (Atom/isZero cord)
                    0
                    (noun [0 cord])))
            pox [0 :http id 0]
            fav [:this ; i think chis is for urb-over-http?
                 1     ; always insecure for now
                 [0 (ip-string-to-atom (:remote-addr req))]
                 med url hed bod]]
        (noun [pox fav])))))

(defn- waiter [http]
  (let [in (chan)]
    (go-loop [channels {}]
      (recur
        (alt! in   ([[id ch]]
                      (assoc channels id ch))
              http ([eff]
                    (let [wir (.head eff)
                          id  (.head (.tail (.tail wir)))
                          ch  (get channels id)]
                      (if (nil? ch)
                        (log/error "http dead request " (Noun/toString id))
                        (>! ch (.tail eff)))
                      (dissoc channels id))))))
    in))

(defn start [poke http port]
  (let [wait (waiter http)]
    (future
      (jetty/run-jetty
        (fn [req]
          (let [client (Atom/stringToCord (name (gensym "http-server-request-id")))
                poke-n (request-to-poke client req)]
            (if (nil? poke-n)
              {:status 500}
              (let [ch (chan)
                    resp (<!! (go (>! wait [client ch])
                                  (>! poke poke-n)
                                  (<! ch)))
                    httr (.tail resp)
                    stat (int (.head httr))
                    tats (.tail httr)
                    hedr (.head tats)
                    unit (.tail tats)
                    body (if-not (Noun/isCell unit) 
                           nil 
                           (let [octs (.tail unit)
                                 siz  (.head octs)
                                 at   (.tail octs)]
                             (ByteArrayInputStream. (Atom/toByteArray at))))]
              {:status  stat
               :body    body
               :headers (reduce 
                          (fn [m pair]
                            (assoc m
                              (Atom/cordToString (.head pair))
                              (Atom/cordToString (.tail pair))))
                            {}
                            (List. hedr))}))))
        {:port port
         :host "127.0.0.1"}))
    nil))
