(ns emr-connect.core
  (:require [clojure.tools.logging :as log]
            [amazonica.aws.ec2 :as ec2]
            [amazonica.aws.s3 :as s3]
            [amazonica.aws.elasticmapreduce :as emr]
            [environ.core :refer [env]])
  (:use [emr-connect.aws]
        [clojure.string :only (join)])
  (:gen-class))

(defn -main
  [& args]
  (let [job-name (env :job-name) log-bucket (join "-" [job-name "logs"])]
    (init-log-bucket job-name)
    (emr/run-job-flow :name job-name
                  :log-uri (join "/" ["s3n:/" log-bucket "logs"])
                  :service-role "EMR_DefaultRole"
                  :job-flow-role "EMR_EC2_DefaultRole"
                  :instances
                    {:instance-groups [
                       {:instance-type "m1.large"
                        :instance-role "MASTER"
                        :instance-count 1
                        :market "SPOT"
                        :bid-price "0.10"}]}
                  :steps [
                    {:name "my-step"
                     :hadoop-jar-step
                       {:jar "s3n://beee0534-ad04-4143-9894-8ddb0e4ebd31/hadoop-jobs/bigml"
                        :main-class "bigml.core"
                        :args ["s3n://beee0534-ad04-4143-9894-8ddb0e4ebd31/data" "output"]}}])
    (emr/list-clusters))
  )
