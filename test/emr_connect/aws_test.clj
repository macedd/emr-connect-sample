(ns emr-connect.aws-test
  (:require [clojure.test :refer :all]
            [emr-connect.aws :refer :all]
            [amazonica.aws.s3 :as s3]
            [amazonica.aws.elasticmapreduce :as emr]
            [environ.core :refer [env]])
  (:use [clojure.string :only (join)]))

(deftest auth-test
  (testing "AWS Authentication"
    (is (= true
          (vector? (s3/list-buckets))))))

(deftest bucket-exists-test
  (testing "S3 Bucket existence"
    (let [bucket-name "emr-connect-test"]
      (is (= false
              (bucket-exists bucket-name)))
      (s3/create-bucket bucket-name)
      
      (is (= true
              (bucket-exists bucket-name)))
      
      (s3/delete-bucket bucket-name)
      (is (= false
              (bucket-exists bucket-name))))))

(deftest init-bucket-test
  (testing "S3 Job Bucket initiation"
    (let [job-name (join "-" ["emr-connect" (System/currentTimeMillis)]) bucket-name job-name]
      (is (= false (bucket-exists bucket-name)))
      (dosync
        (init-bucket job-name)
        (is (= true (bucket-exists bucket-name)))
        (s3/delete-bucket bucket-name)))))

(deftest run-job-test
  (testing "EMR Job Creation"
    (let [job-name (env :job-name) bucket-name job-name running (running-jobs job-name)]
      (init-bucket job-name)
      (run-job job-name)
      (is (> (running-jobs job-name) running))
      ; (s3/delete-bucket bucket-name)
    )))