(ns dbpedia-media-extractor.core
 (:gen-class)
 (:require
  [oauth.client :as oc]))


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
  "Returns a map with the stored credentials (such as API key, secret, access token, access secret).
  'stored-credentials-csv-file' should be a CSV file such as 'resources/flickr_{keys,oauth_token}.csv'"
  [stored-credentials-csv-file]
  (first (mapify (parse (slurp stored-credentials-csv-file)))))

(defn flickr-service
  "Create Flickr service, given my-api-key and my-api-secret"
  [my-api-key my-api-secret]
  (oc/make-consumer my-api-key
                    my-api-secret
                    "https://www.flickr.com/services/oauth/request_token"
                    "https://www.flickr.com/services/oauth/access_token"
                    "https://www.flickr.com/services/oauth/authorize"
                    :hmac-sha1))

(defn generate-access-token
  "Generates an access token vector, based on credentials stored in a CSV file. Needs interaction to get the authorization."
  [stored-credentials-csv-file] ;; This one should be "resources/flickr_keys.csv"
  (let [my-api-key     (:api_key (stored-credentials stored-credentials-csv-file))
        my-api-secret  (:api_secret (stored-credentials stored-credentials-csv-file))
        #_             (println "my-api-key: " my-api-key)
        #_             (println "my-api-secret: " my-api-secret)
        service        (flickr-service my-api-key my-api-secret)
        #_             (println "service: " service)
        rec            (oauth/new-record service)
        #_             (println "rec: " rec)
        _              (println)
        _              (println "Please, follow the Authorization URL below to authorize this app on Flickr.")
        _              (println "Once that is done, please enter the verifier obtained from Flickr.")
        _              (println)
        _              (println "Authorization URL: "(:url rec))
        _              (println)
        _              (print "Verifier ('NNN-NNN-NNN'): ")
        _              (flush)
        verifier          (clojure.string/trim (read-line))
        _              (println "Thank you!")
        #_             (println "This is the verifier you obtained: " verifier)
        rec            (oauth/activate service rec verifier)
        #_             (println "Authorized rec: " rec)
        access-token   (:access-token rec)
        #_             (println "This is the Access Token (to be stored): " access-token)]
    access-token))

(def flickr-root-method-path
  "https://api.flickr.com/services/rest/")

(defn invoke-flickr-method
  "Invokes a Flickr method (currently, it signs all requests due to a qarth bug)"
  [method-path sign-request? api-key api-secret oauth-token oauth-secret]
  (let [service       (flickr-service api-key api-secret)
        access-token  {:access-token [oauth-token oauth-secret]}
        #_        (println "access-token: " access-token)
        #_        (println "service: " service)
        ;; FIXME: 'sign-request?' flag is currently ignored due to a qarth 0.1.3 library bug
        ;;                                                    --Leandro Doctors, 2017-10-27
        resp     ((oauth/requestor service access-token)
                  {:url (str flickr-root-method-path
                             "?method=" method-path
                             "&api_key=" api-key
                             "&format=json&nojsoncallback=1")})]
    resp))

(defn perform-flickr-search
 "Performs a simple search on Flickr. It supports geographical restrictions."
 [method-path sign-request? api-key api-secret oauth-token oauth-secret search-text latitude longitude radius results-per-query target-licenses]
 (let [service       (flickr-service api-key api-secret)
       access-token  {:access-token [oauth-token oauth-secret]}
       #_        (println "access-token: " access-token)
       #_        (println "service: " service)
       ;; FIXME: 'sign-request?' flag is currently ignored due to a qarth 0.1.3 library bug
       ;;                                                    --Leandro Doctors, 2017-10-27
       resp     ((oauth/requestor service access-token)
                 {:url (str flickr-root-method-path
                            "?method=" method-path
                            "&api_key=" api-key
                            "&text=" search-text
                            "&lat=" latitude
                            "&lon=" longitude
                            "&radius=" radius
                            "&license=" target-licenses
                            "&per_page=" results-per-query
                            "&radius_units=km"
                            "&sort=relevance"
                            "&min_taken_date=1800-01-01 00:00:00"
                            "&format=json&nojsoncallback=1")})]
   resp))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
