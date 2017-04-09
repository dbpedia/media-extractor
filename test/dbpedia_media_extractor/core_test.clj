(ns dbpedia-media-extractor.core-test
  (:require [clojure.test :refer :all]
            [dbpedia-media-extractor.core :refer :all]))

(deftest input-reading-test
  (testing "Parsing a file."
    (is (= generated-data mocked-data))))
