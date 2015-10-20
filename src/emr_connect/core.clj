(ns emr-connect.core
  (:require [clojure.tools.logging :as log]
            [amazonica.aws.ec2 :as ec2])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (log/info "Right here!")
  (println "Hello, World!")
  (ec2/describe-instances))
