(defproject jaque "0.1.0-SNAPSHOT"
  :description "Clojure implementation of nock"
;  :jvm-opts    ["-Dcom.sun.management.jmxremote"
;                "-Dcom.sun.management.jmxremote.ssl=false"
;                "-Dcom.sun.management.jmxremote.authenticate=false"
;                "-Dcom.sun.management.jmxremote.port=43210"]
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :dependencies [[primitive-math "0.1.3"]
                 [criterium "0.4.4"]
                 [org.clojure/clojure "1.8.0"]])
