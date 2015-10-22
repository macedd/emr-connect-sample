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
  (let [job-name (env :job-name)]
    (init-bucket job-name)
    (run-job job-name)
    )
  )
