(defproject jaque "0.1.0-SNAPSHOT"
  :description "Clojure implementation of nock"
  :jvm-opts     ["-server"
                 "-XX:+UnlockExperimentalVMOptions" 
                 "-XX:+EnableJVMCI"
                 "-d64" 
                 "-Djvmci.class.path.append=/home/pdriver/graal/graal-core/mxbuild/dists/graal.jar"
                 "-Xbootclasspath/a:/home/pdriver/graal/truffle/mxbuild/dists/truffle-api.jar"]
;  :jvm-opts    ["-Dcom.sun.management.jmxremote"
;                "-Dcom.sun.management.jmxremote.ssl=false"
;                "-Dcom.sun.management.jmxremote.authenticate=false"
;                "-Dcom.sun.management.jmxremote.port=43210"]
  :main         jaque.vm
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.oracle.truffle/truffle-api "0.22"]
                 [com.oracle.truffle/truffle-dsl-processor "0.22"]
;                 [com.oracle.truffle/truffle-tck "0.20"]
;                 [primitive-math "0.1.3"]
;                 [criterium "0.4.4"]
                 [slingshot "0.12.2"]])
