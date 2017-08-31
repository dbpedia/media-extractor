(ns dbpedia-media-extractor.core
  (:gen-class))

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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
