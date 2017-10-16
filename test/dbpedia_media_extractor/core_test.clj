(ns dbpedia-media-extractor.core-test
  (:require [clojure.test :refer :all]
            [dbpedia-media-extractor.core :refer :all]
            [clojure.java.io :as io]))

(deftest input-parsing-test
  (testing "Parsing a file"
    (let [raw-data "name,age\nBart,10\nLisa,8"
          tmp-filename "resources/simpson_kids.csv"
          correctly-parsed-data '(["name" "age"] ["Bart" "10"] ["Lisa" "8"])]
      (spit tmp-filename raw-data)
      (is (= (parse (slurp tmp-filename)) correctly-parsed-data) "Testing the parser")
      (io/delete-file tmp-filename))))

(deftest input-mapifying-test
  (testing "Mapifying a CSV file (with header)"
    (let [raw-data '(["name" "age"] ["Bart" "10"] ["Lisa" "8"])
          correctly-mapified-data '({:name "Bart", :age "10"} {:name "Lisa", :age "8"})]
      (is (= (mapify raw-data) correctly-mapified-data)))))

#_(deftest generate-access-token-test
   (testing "Generating Flickr OAuth Access Token"
     (let [stored-access-token-csv-file "resources/flickr_oauth_token.csv"
           stored-credentials-csv-file  "resources/flickr_keys.csv"
           generated-access-token       (generate-access-token stored-credentials-csv-file)
           parsed-access-token          (second (parse (slurp stored-access-token-csv-file)))]
           ;;stored-token                 (:oauth_token (mapify pre-generated-acces-token))
           ;;stored-secret                (:oauth_secret (mapify pre-generated-acces-token))
         (is (= parsed-access-token generated-access-token)))))

(deftest invoke-flickr-test-echo-test
  (testing "Invoking 'flickr.test.echo' (it requires no authentication; only the API key)."
    (let [api-key  (:api_key (stored-credentials "resources/flickr_keys.csv"))
          response (invoke-flickr-test-echo api-key)
          _        (println "Response: " response)]
      (is (= (:stat response) "ok")))))
