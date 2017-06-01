(ns dbpedia-media-extractor.core-test
  (:require [clojure.test :refer :all]
            [dbpedia-media-extractor.core :refer :all]
            [clojure.java.io :as io]
            [qarth.oauth :as oauth]
            [qarth.impl.scribe]
            [clojure.data.json :as json]
            [clojure.data.xml]))

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
      (is (= (mapify raw-data) correctly-mapified-data)))))

(deftest flickr-oauth-login
  (testing "Logging into Flick."

    (def credentials-csv-file "resources/flickr_keys.csv")

    (def mapped-login-credentials (first (mapify (parse (slurp credentials-csv-file)))))

    (def conf {:type       :scribe
               :provider   org.scribe.builder.api.FlickrApi
               :api-key    (:api_key mapped-login-credentials)
               :api-secret (:secret mapped-login-credentials)})

    (def service (oauth/build conf))

    (let [rec               (oauth/new-record service)
          _                 (println "Auth url:" (:url rec))
          _                 (print "Enter token: ")
          _                 (flush)
          token             (clojure.string/trim (read-line))
          rec               (oauth/activate service rec token)
          resp              ((oauth/requestor service rec)
                             {:url "https://api.flickr.com/services/rest/"})
          flickr-test-login (-> resp :body clojure.data.xml/parse-str
                                :content first :content first)
          _                 (println "response status:" (:status resp))
          _                 (println "response headers:" (pr-str (:headers resp)))
          _                 (println "flickr-test-login:" flickr-test-login)])))
