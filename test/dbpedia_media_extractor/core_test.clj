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
  (testing "Invoking 'flickr.test.echo'."
    (let [stored-flickr-keys-csv-file         "resources/flickr_keys.csv"
          sign-request?                       true
          api-key                             (:api_key (stored-credentials stored-flickr-keys-csv-file))
          api-secret                          (:api_secret (stored-credentials stored-flickr-keys-csv-file))
          stored-flickr-oauth-token-csv-file  "resources/flickr_oauth_token.csv"
          oauth-token                         (:oauth_token (stored-credentials stored-flickr-oauth-token-csv-file))
          oauth-secret                        (:oauth_secret (stored-credentials stored-flickr-oauth-token-csv-file))
          response                            (invoke-flickr-method "flickr.test.echo" sign-request? api-key api-secret oauth-token oauth-secret)]
          ;;_                                   (println "**Response**")
          ;;_                                   (println response)]
      (is (= (:status response) 200)))))

(deftest perform-flickr-search-test
  (testing "Invoking 'flickr.photos.search' with geographical coordinates"
    (let [stored-flickr-keys-csv-file         "resources/flickr_keys.csv"
          sign-request?                       true
          api-key                             (:api_key (stored-credentials stored-flickr-keys-csv-file))
          api-secret                          (:api_secret (stored-credentials stored-flickr-keys-csv-file))
          stored-flickr-oauth-token-csv-file  "resources/flickr_oauth_token.csv"
          oauth-token                         (:oauth_token (stored-credentials stored-flickr-oauth-token-csv-file))
          oauth-secret                        (:oauth_secret (stored-credentials stored-flickr-oauth-token-csv-file))
          search-text                         "Brussels"
          latitude                            "50.85"
          longitude                           "4.35"
          radius                              "5"
          results-per-query                   "3" ; Max. 30, according to Flickr's TOU
          target-licenses                     "4,5,7,8"
          response                            (perform-flickr-search "flickr.photos.search" sign-request? api-key api-secret oauth-token oauth-secret search-text latitude longitude radius results-per-query target-licenses)]
          ;;_                                   (println "**Response**")
          ;;_                                   (println response)]
      (is (= (:status response) 200)))))
