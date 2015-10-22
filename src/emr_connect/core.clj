(ns emr-connect.core
  (:require [environ.core :refer [env]])
  (:use [emr-connect.aws])
  (:gen-class))

(defn -main
  [& args]
  (let [job-name (env :job-name)]
    (init-bucket job-name)
    (run-job job-name)))
