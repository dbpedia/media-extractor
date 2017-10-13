(ns dbpedia-media-extractor.core
  (:gen-class)
  (:require
    [qarth.oauth :as oauth]
    [qarth.impl.scribe]))


(defn parse
  "Convert a CSV into rows of columns"
  [string]
  (map #(clojure.string/split % #",")
       (clojure.string/split string #"\n")))

(defn mapify
  "Convert a seq of rows (vectors) of string pairs into a seq of maps like {:name \"Edward Cullen\" :glitter-index \"10\"}"
  [rows]
  (let [my-keywords (map keyword (first rows))]
    (map (fn [unmapped-row]
           (reduce (fn [row-map [my-key my-value]]
                     (assoc row-map my-key my-value))
                   {}
                   (map vector my-keywords unmapped-row)))
         (rest rows))))

(defn stored-credentials
  "Returns a map with the stored API key and secret"
  [stored-credentials-csv-file] ;; This one should be "resources/flickr_keys.csv"
  (first (mapify (parse (slurp stored-credentials-csv-file)))))

(defn generate-access-token
  "Generates an access token vector, based on credentials stored in a CSV file. Needs interaction to get the authorization."
  [stored-credentials-csv-file] ;; This one should be "resources/flickr_keys.csv"
  (let [my-api-key     (:api_key (stored-credentials stored-credentials-csv-file))
        my-api-secret  (:api_secret (stored-credentials stored-credentials-csv-file))
        #_             (println "my-api-key: " my-api-key)
        #_             (println "my-api-secret: " my-api-secret)
        conf           {:type :scribe, :provider org.scribe.builder.api.FlickrApi, :api-key my-api-key, :api-secret my-api-secret}
        #_             (println "conf: " conf)
        service        (oauth/build conf)
        #_             (println "service: " service)
        rec            (oauth/new-record service)
        #_             (println "rec: " rec)
        _              (println)
        _              (println "Please, follow the Authorization URL below to authorize this app on Flickr.")
        _              (println "Once that is done, please enter the token obtained from Flickr.")
        _              (println)
        _              (println "Authorization URL: "(:url rec))
        _              (println)
        _              (print "Token ('NNN-NNN-NNN'): ")
        _              (flush)
        token          (clojure.string/trim (read-line))
        _              (println "Thank you!")
        #_             (println "This is the token you obtained: " token)
        rec            (oauth/activate service rec token)
        #_             (println "Authorized rec: " rec)
        access-token   (:access-token rec)
        #_             (println "This is the Access Token (to be stored): " access-token)]
    access-token))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
