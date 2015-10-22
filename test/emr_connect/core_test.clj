(ns emr-connect.core-test
  (:require [clojure.test :refer :all]
            [emr-connect.core :refer :all]
            [clojure.tools.logging :refer :all :as log]
            [clojure.java.io :as io]
            [amazonica.aws.s3 :as s3])
  (:use [clojure.string :only (join)]))

(deftest a-test
  (testing "FIXME, I (dont) fail."
    (is (= 1 1))))

(deftest log-test
  (testing "Log file writing."
    ; log file path
    (def logfile "./log/logger.log")
    ; string to log and test
    (def logstring (join " " ["Here testing on" (str (System/currentTimeMillis))] ))
    ; insert the log string
    (log/info logstring)
    ; check log file exists
    (is (= true 
            (.exists (io/as-file logfile))))
    ; load log contents
    (def logcontent (slurp logfile))
    ; check string logged is in the log file
    (is (= true
          (boolean (.contains logcontent logstring))))))
