(ns jaque.kernel)

(defn- kernel-axis [k axis]
  (.nock (:context k)
         (:arvo k)
         (noun [9 axis 0 1])))

(defn- slam [k gate sample]
  (.slam (:context k) gate (noun sample)))

(defn- wish [k s]
  (slam k (kernel-axis 20) (Atom/stringToCord s)))

(defn- kernel-call [k n s]
  (slam k (wish k n) s))

(defn- boot [ctx pill slog]
  (let [ken (.head pill)
        roc (.tail pill)
        cor (.nock ctx 0 ken) ; "to bind jets"
        da  (Time/now)
        uv  (Noun/mug da)
        k   {:sev uv
             :now da
             :arvo roc
             :context ctx
             :slog slog}]
    (assoc k
      :wen (kernel-call k "scot" [:da da])
      :sen (kernel-call k "scot" [:uv uv]))))

(defn- atom-from-file [file]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (io/copy (io/input-stream file) out)
    (-> out (.toByteArray)
        (Atom/fromByteArray Atom/LITTLE_ENDIAN))))

(defn- path-to-noun [base path]
  (let [nested   (map (fn [p]
                        (let [s (.toString p)]
                          (string/split s #"\.")))
                      path)
        cords    (map #(Atom/stringToCord %) (flatten nested))
        pax      (seq->it cords)
        file     (.toFile (.resolve base path))
        mim      [:text :plain 0]
        size     (.length file)
        contents (atom-from-file file)
        dat      [mim size contents]]
    (noun [pax 0 dat])))

(defn- home-sync [m dirpath]
  (let [f    (io/file dirpath)
        base (Paths/get (.toURI f))]
    (if (or (nil? f)
            (not (.isDirectory f)))
      (do (log/error "bad initial sync directory")
          m)
      (let [files (filter #(.isFile %) (file-seq f))
            rels  (map #(.relativize base (Paths/get (.toURI %))) files)
            vis   (filter (fn [path] (not-any? #(.startsWith (.toString %) ".") path)) rels)
            can   (seq->it (map (partial path-to-noun base) vis))
            pax   [0 :sync (:sen m) 0]
            fav   [:into 0 0 can]]
        (noun [pax fav])))))

(defn- boot-poke [k ech event]
  (let [ctx (:context k)
        old (.caller ctx)]
    (set! (.caller ctx) 
          (reify Caller
            (kernel [this gate-name sample]
              (kernel-call k gate-name sample))
            (slog [this tank]
              (>!! (:slog k)))))
    (let [res (slam k (kernel-axis k 42) event)
          eff (.head r)
          arv (.tail r)]
      (set! (.caller ctx) old)
      (onto-chan ech (List. eff))
      arv)))

; poke: we read poke nouns and feed them to arvo
; eff:  we write effects, doing no dispatching
; tank: we write tanks to be printed somewhere
(defn start [poke eff tank {:keys [jets profile system-pill sync-dir]}]
  (let [ctx (Context. jets profile)
        sink (reify Consumer
               (accept [t] (>!! tank t)))
        sys (System. ctx sink)
        pre (PrevaylerFactory/createPrevayler sys)]
    (when (nil? (.arvo sys))
      (let [k (-> (boot ctx system-pill tank)
                  (boot-poke eff [[0 :newt (:sen m) 0] :barn 0])
                  (boot-poke eff [[0 :term :1 0] :boot :sith 0 0 0])
                  (boot-poke eff [0 :verb 0])
                  (boot-poke eff (home-sync sync-dir)))]
        (.execute pre (Boot. (:arvo k) (:now k) (:wen k) (:sen k) (:sev k)))))
    (go-loop []
      (>! eff (.execute pre (Poke. (<! poke))))
      (recur))))
