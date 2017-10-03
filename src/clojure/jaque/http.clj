(ns jaque.http
  (:use [jaque.noun])
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [clojure.string :as string]
            [org.httpkit.client :as client]
            [org.httpkit.server :as server])
  (:import java.io.ByteArrayInputStream
           (java.net URI InetAddress)
           (net.frodwith.jaque.data Atom Noun Trel List)))

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

(defn handle-req [poke effects stop req]
  (server/with-channel req ch
    (let [cname  (Atom/stringToCord (name (gensym "http-server-request")))
          wire   (noun [0 :http cname 0])
          poke-n (request-to-poke wire req)]
      (if (nil? poke-n)
        (do (server/send! ch {:status 500})
            (server/close ch))
        (let [rch (async/chan)]
          (async/sub effects wire rch)
          (async/go
            (if-not (async/>! poke poke-n)
              (stop)
              (let [res (async/<! rch)]
                (if (nil? res)
                  (stop)
                  (let [httr (.tail (.tail res))
                        stat (int (.head httr))
                        tats (.tail httr)
                        hedr (.head tats)
                        hmap (reduce
                               (fn [m pair]
                                 (let [k (Atom/cordToString (.head pair))
                                       v (Atom/cordToString (.tail pair))]
                                   (assoc m k v)))
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
                    (server/send! ch {:status stat, :headers hmap, :body body})
                    (server/close ch)))))))))))

(defn- purl-to-uri [purl]
  (let [purl (Trel/expect purl)
        hart (Trel/expect (.p purl))
        sec  (.p hart)
        port (.q hart)
        host (.r hart)
        pork (.q purl)
        quay (.r purl)
        hoss (if (= Atom/YES (.head host))
               (string/join "." (reverse (map #(Atom/cordToString %) (List. (.tail host)))))
               (str (InetAddress/getByAddress (Atom/toByteArray (.tail host)))))
        scem (if (= sec Atom/YES) "https" "http")
        foo  (log/debug "scem:" scem)
        auth (if (= port 0)
               hoss
               (format "%s:%d" hoss (.tail port)))
        path (str "/"
                  (let [jon (string/join "/" (map #(Atom/cordToString %) (List. (.tail pork))))]
                    (if (= 0 (.head pork))
                      jon
                      (format "%s.%s" jon (Atom/cordToString (.head pork))))))
        pars (map (fn [p]
                    (let [k (.head p), v (.tail p)]
                      (if (= 0 v)
                        (Atom/cordToString k)
                        (format "%s=%s" (Atom/cordToString k) (Atom/cordToString v)))))
                  (List. quay))
        quer (string/join "&" pars)]
    (URI. scem auth path quer "")))

(defn- start-client [poke reqs]
  (async/go-loop []
    (let [e (async/<! reqs)]
      (if (nil? e)
        (log/debug "http client shutting down")
        (let [pax  (.head e)
              fav  (.tail e)
              seqn (.head (.tail fav))
              unit (.tail (.tail fav))]
          (if (= unit 0)
            (do (log/warn "thus: cancel?")
                (recur))
            (let [hiss (.tail unit)
                  moth (Trel/expect (.tail hiss))
                  meth (.p moth)
                  math (.q moth)
                  uoct (.r moth)
                  opts {:url (str (purl-to-uri (.head hiss)))
                        :as :byte-array
                        :method (keyword (Atom/cordToString meth))
                        :body (if (= 0 uoct)
                                nil
                                (Atom/toByteArray (.tail (.tail uoct))))
                        :headers (reduce (fn [m p]
                                           (let [k  (Atom/cordToString (.head p))
                                                 vs (map #(Atom/cordToString %) (.tail p))]
                                             (assoc m k (string/join "," vs))))
                                         {} (nlr->seq math))}]
              (client/request opts
                (fn [{:keys [status headers body]}]
                  (let [mess (seq->it (map (fn [[k v]]
                                             (noun [(Atom/stringToCord (capitalize (name k)))
                                                    (Atom/stringToCord v)]))
                                           headers))
                        uoct (if (nil? body)
                               0
                               (noun [0 (alength body) (Atom/fromByteArray body)]))
                        httr [:they seqn (long status) mess uoct]]
                    (async/put! poke (noun [pax httr])))))
              (recur))))))))

(defn start [{poke :poke-channel, effects :effect-pub, port :port, sen :sen}]
  (let [satom (atom nil)
        stop  #(let [f @satom]
                 (when-not (nil? f)
                   (swap! satom (fn [&_] nil))
                   (log/debug "http server shutdown")
                   (f)))
        app   (partial handle-req poke effects stop)
        wire  (noun [0 :http sen 0])
        reqs  (async/chan)
        ret   (server/run-server app {:port port, :ip "127.0.0.1"})]
    (async/sub effects wire reqs)
    (start-client poke reqs)
    (async/put! poke (noun [wire :born 0]))
    (swap! satom (fn [&_] ret))
    stop))
