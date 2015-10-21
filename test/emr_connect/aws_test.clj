(ns emr-connect.aws-test
  (:require [clojure.test :refer :all]
            [emr-connect.aws :refer :all]
            [amazonica.aws.s3 :as s3])
  (:use [clojure.string]))

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

(deftest init-log-bucket-test
  (testing "S3 Log Bucket initiation"
    (let [job-name (join "-" ["emr-connect" (System/currentTimeMillis)]) bucket-name (join "-" [job-name "logs"])]
      (is (= false (bucket-exists bucket-name)))
      (dosync
        (init-log-bucket job-name)
        (is (= true (bucket-exists bucket-name)))
        (s3/delete-bucket bucket-name)))))