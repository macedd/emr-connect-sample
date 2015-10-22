(ns emr-connect.aws
  (:require [amazonica.aws.elasticmapreduce :as emr]
            [amazonica.aws.s3 :as s3]
            [amazonica.aws.ec2 :as ec2]
            [clojure.tools.logging :refer :all :as log])
  (:import [com.amazonaws.services.s3.model
              Grant
              Grantee
              GroupGrantee
              Permission
              Owner
              AccessControlList])
  (:use [amazonica.core :only (coerce-value)]
        [clojure.string :only (join)])
  (:gen-class))

(defn bucket-exists [bucket-name]
  "Check if bucket name exists in the user S3"
  (let [buckets (s3/list-buckets)]
    (= 1
      (count
        (filter
          #(= bucket-name (:name %))
          buckets)))))

(defn init-bucket [job-name]
  "Create bucket for the job and set its permission / configuration"
  ; (let [bucket-name (join "-" [job-name "logs"])]
    (let [bucket-name job-name]
      (log/info ["Initing job bucket" bucket-name])
      (cond
        (= false (bucket-exists bucket-name))
          (do
            (s3/create-bucket bucket-name)
            
            (let [acl (new AccessControlList) group (coerce-value "LogDelivery" Grantee) curr (s3/get-bucket-acl bucket-name)]
              (.grantPermission acl group (coerce-value "Write" Permission))
              (.grantPermission acl group (coerce-value "READ_ACP" Permission))
              (.setOwner acl (coerce-value (:owner curr) Owner))
              (s3/set-bucket-acl bucket-name acl))

            (s3/set-bucket-logging-configuration  :bucket-name bucket-name
                                                  :logging-configuration
                                                    {:log-file-prefix "hadoop-job_"
                                                     :destination-bucket-name bucket-name}))
        :else
          (log/info ["Log bucket" bucket-name "already exists"]))))

(defn run-job [job-name]
  "Run the EMR Job"
  (let [bucket job-name]
    (emr/run-job-flow
      :name job-name
      :log-uri (join "/" ["s3n:/" bucket "logs"])
      :service-role "EMR_DefaultRole"
      :job-flow-role "EMR_EC2_DefaultRole"

      :instances
        {:instance-groups [
           {:instance-type "m1.small"
            :instance-role "MASTER"
            :instance-count 1
            :market "ON_DEMAND"}]}

      :steps [
        {:name "sample-hadoop-streaming"
         ; :action-on-failure "CANCEL_AND_WAIT"
         :hadoop-jar-step
           {:jar "/usr/lib/hadoop-mapreduce/hadoop-streaming.jar"
            :main-class "bigml.core"
            :args [(join "/" ["s3n:/" bucket "data"]) "output"
                  "s3://elasticmapreduce/samples/wordcount/input" "input"
                  "s3://elasticmapreduce/samples/wordcount/wordSplitter.py" "mapper"
                  "aggregate" "reducer"]}}])
  ))

(defn running-jobs [job-name]
  "Number of jobs running"
  (count
    (filter
      #(= job-name (:name %))
      (:clusters (emr/list-clusters)))))