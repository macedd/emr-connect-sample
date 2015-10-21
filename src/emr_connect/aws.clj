(ns emr-connect.aws
  (:require [amazonica.aws.elasticmapreduce :as emr]
            [amazonica.aws.s3 :as s3]
            [clojure.tools.logging :refer :all :as log])
  (:import [com.amazonaws.services.s3.model
              Grant
              Grantee
              GroupGrantee
              Permission
              Owner
              AccessControlList])
  (:use [amazonica.core :only (coerce-value)])
  (:gen-class))

(defn bucket-exists [bucket-name]
  "Check if bucket name exists in current user S3"
  (let [buckets (s3/list-buckets)]
    (= 1
      (count
        (filter
          #(= bucket-name (:name %))
          buckets)))))

(defn init-log-bucket [name]
  "Create bucket for logs and set its permission / configuration"
  (let [bucket-name (clojure.string/join "-" [name "logs"])]
    (log/info ["Initing log bucket" bucket-name])
    (cond
      (= false (bucket-exists bucket-name))
        (do
          (s3/create-bucket :bucket-name bucket-name
                            :access-control-list
                              {:grant-permission ["LogDelivery" "Write"]})
          
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

