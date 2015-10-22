(defproject emr-connect "0.1.0-SNAPSHOT"
  :description "App conectando Amazon ElasticMapReduce"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [amazonica "0.3.34"]
                 [environ "1.0.1"]
                 ;; LOGGING DEPS
                 [org.clojure/tools.logging "0.2.4"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [log4j/log4j "1.2.17"]]
  :main ^:skip-aot emr-connect.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-environ "1.0.1"]])
