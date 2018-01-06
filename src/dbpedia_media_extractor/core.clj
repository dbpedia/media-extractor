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

(defn stored-credentials-map
  "Returns a map with the stored credentials (such as API key, secret, access token, access secret).
  'stored-credentials-csv-file' should be a CSV file such as 'resources/flickr_{keys,oauth_token}.csv'"
  [stored-credentials-csv-file]
  (first (mapify (parse (slurp stored-credentials-csv-file)))))

(defrecord OAuthConsumerKey [api_key api_secret])

(defrecord OAuthToken [oauth_token oauth_token_secret])

(defn make-flickr-consumer
  "Create Flickr service, given my-api-key and my-api-secret"
  [{:keys [api_key api_secret]}]
  (oc/make-consumer api_key
                    api_secret
                    "https://www.flickr.com/services/oauth/request_token"
                    "https://www.flickr.com/services/oauth/access_token"
                    "https://www.flickr.com/services/oauth/authorize"
                    :hmac-sha1))

(defn generate-access-token
  "Generates an access token record, based on credentials stored in a CSV file. Needs interaction to get the authorization."
  [stored-credentials-csv-file] ;; This one should be "resources/flickr_keys.csv"
  (let [creds           (stored-credentials-map stored-credentials-csv-file)
        consumer-key    (map->OAuthConsumerKey creds)
        #_               (println "consumer-key: " consumer-key)
        flickr-consumer (make-flickr-consumer consumer-key)
        #_               (println "flickr-consumer: " flickr-consumer)
        request-token   (oc/request-token flickr-consumer)
        #_               (println "request-token: " request-token)
        _               (println)
        _               (println "Please, follow the Authorization URL below to authorize this app on Flickr.")
        _               (println "Once that is done, please enter the verifier obtained from Flickr.")
        _               (println)
        _               (println "Authorization URL: "(oc/user-approval-uri flickr-consumer
                                                                           (:oauth_token request-token)))
        _               (println)
        _               (print "Verifier ('NNN-NNN-NNN'): ")
        _               (flush)
        verifier        (clojure.string/trim (read-line))
        _               (println "Thank you!")
        #_               (println "This is the verifier you obtained: " verifier)
        access-token    (select-keys (oc/access-token flickr-consumer request-token verifier) [:oauth_token :oauth_token_secret])
        _               (println "Access-token: " access-token)
        acc-tok-rec     (map->OAuthToken access-token)
        _               (println "This is the Access Token (to be stored): " acc-tok-rec)]
    acc-tok-rec))

(def flickr-root-method-path
  "https://api.flickr.com/services/rest/")

(defn invoke-flickr-method
  "Invokes a Flickr method"
  [method-path sign-request? api-key api-secret oauth-token oauth-secret]
  (let [flickr-consumer (make-flickr-consumer api-key api-secret)
        access-token    {:access-token [oauth-token oauth-secret]}
        #_               (println "access-token: " access-token)
        #_               (println "flickr-consumer: " flickr-consumer)
        creds           (oc/credentials flickr-consumer oauth-token oauth-secret :POST flickr-root-method-path {:method method-path :api_key api-key})
        user-params     {:format "json&nojsoncallback=1"}
        resp            (clj-http.client/post flickr-root-method-path {:query-params (merge creds user-params)})]
    resp))

(defn perform-flickr-search
  "Performs a simple search on Flickr. It supports geographical restrictions."
  [method-path sign-request? api-key api-secret oauth-token oauth-secret search-text latitude longitude radius results-per-query target-licenses]
  (let [flickr-consumer  (make-flickr-consumer api-key api-secret)
        access-token     {:access-token [oauth-token oauth-secret]}
        #_                (println "access-token: " access-token)
        #_                (println "service: " service)
        creds            (oc/credentials flickr-consumer oauth-token oauth-secret :POST flickr-root-method-path {:method method-path :api_key api-key})
        user-params      {:format "json&nojsoncallback=1" :text search-text :lat latitude :lon longitude :radius radius :license target-licenses :per_page results-per-query :radius_units "km" :sort "relevance" :min_taken_date "1800-01-01 00:00:00"}
        resp             (clj-http.client/post flickr-root-method-path {:query-params (merge creds user-params)})]
    resp))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
