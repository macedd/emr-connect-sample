(ns aws-teste.core-test
  (:require [clojure.test :refer :all]
            [aws-teste.core :refer :all]
            [clojure.tools.logging :refer :all :as log]
            [clojure.java.io :as io])
  (:use [clojure.string]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest log-test
  (testing "Log file writing."
    (def logfile "./log/logger.log")
    (def logstring (join " " ["Here testing on" (str (System/currentTimeMillis))] ))
    (log/info logstring)
    (is (= true 
            (.exists (io/as-file logfile))))
    (def logcontent (slurp logfile))
    (is (= true
          (boolean (.contains logcontent logstring))))))
