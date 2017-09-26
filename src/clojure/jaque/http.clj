(ns jaque.http
  (:use [jaque.noun])
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [clojure.string :as string]
            [org.httpkit.server :as http])
  (:import java.io.ByteArrayInputStream
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

(defn request-to-poke [pox req]
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
            bod (let [bod  (:body req)]
                  (if (nil? bod)
                    0
                    (let [cord (Atom/stringToCord (slurp (:body req)))]
                      (if (Atom/isZero cord)
                        0
                        (noun [0 (long (alength cord)) cord])))))
            fav [:this ; i think chis is for urb-over-http?
                 1     ; always insecure for now
                 [0 (ip-string-to-atom (:remote-addr req))]
                 med url hed bod]]
        (noun [pox fav])))))

(defn start [{poke :poke-channel, effects :effect-pub, port :port}]
  (let [shutdown (atom nil)
        app      (fn [req]
                   (http/with-channel req ch
                     (let [client (Atom/stringToCord (name (gensym "http-server-request")))
                           wire   (noun [0 :http client 0])
                           poke-n (request-to-poke wire req)]
                       (if (nil? poke-n)
                         (do (http/send! ch {:status 500})
                             (http/close ch))
                         (let [rch (async/chan)]
                           (async/sub effects wire rch)
                           (log/debug (Noun/toString poke-n))
                           (async/go
                             (if-not (async/>! poke poke-n)
                               (do (log/debug "http server shutdown")
                                   (@shutdown))
                               (let [res (async/<! rch)]
                                 (if (nil? res)
                                   (do (log/debug "http server shutdown")
                                       (@shutdown))
                                   (let [httr (.tail (.tail res))
                                         stat (int (.head httr))
                                         tats (.tail httr)
                                         hedr (.head tats)
                                         hmap (reduce 
                                                (fn [m pair]
                                                  (assoc m
                                                         (Atom/cordToString (.head pair))
                                                         (Atom/cordToString (.tail pair))))
                                                {}
                                                (List. hedr))
                                         unit (.tail tats)
                                         body (if-not (Noun/isCell unit)
                                                nil
                                                (let [octs (.tail unit)
                                                      siz  (.head octs)
                                                      at   (.tail octs)]
                                                  (ByteArrayInputStream. (Atom/toByteArray at))))]
                                     (async/close! rch)
                                     (http/send! ch {:status stat, :headers hmap, :body body})
                                     (http/close ch)))))))))))
        shut-fn  (http/run-server app {:port port, :ip "127.0.0.1"})]
    (swap! shutdown (fn [old & args] shut-fn))))
