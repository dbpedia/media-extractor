(ns dbpedia-media-extractor.core-test
  (:require [clojure.test :refer :all]
            [dbpedia-media-extractor.core :refer :all]))

(deftest input-reading-test
  (testing "Parsing a file."
    (require '[clojure.java.io :as io])
    (def raw-data "name,age\nBart,10\nLisa,8")
    (def tmp-filename "tmp/simpson_kids.csv")
    (spit tmp-filename raw-data)
    (def correctly-parsed-data (["name" "age"] ["Bart" "10"] ["Lisa" "8"]))
    (is (= (parse(tmp-filename)) (correctly-parsed-data)))))
