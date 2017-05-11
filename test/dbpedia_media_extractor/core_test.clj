(ns dbpedia-media-extractor.core-test
  (:require [clojure.test :refer :all]
            [dbpedia-media-extractor.core :refer :all]
            [clojure.java.io :as io]))

(deftest input-parsing-test
  (testing "Parsing a file."
    (let [raw-data "name,age\nBart,10\nLisa,8"
          tmp-filename "./simpson_kids.csv"
          correctly-parsed-data '(["name" "age"] ["Bart" "10"] ["Lisa" "8"])]
      (spit tmp-filename raw-data)
      (is (= (parse (slurp tmp-filename)) correctly-parsed-data) "Testing the parser")
      (io/delete-file tmp-filename))))

(deftest input-mapifying-test
  (testing "Mapifying a CSV file (with header)"
    (let [raw-data '(["name" "age"] ["Bart" "10"] ["Lisa" "8"])
          correctly-mapified-data '({:name "Bart", :age "10"} {:name "Lisa", :age "8"})]
      (is (= (mapify raw-data) correctly-mapified-data))
      )))
