(def project 'dbpedia-media-extractor)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.8.0"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [qarth "0.1.3"]])

(task-options!
 aot {:namespace   #{'dbpedia-media-extractor.core}}
 pom {:project     project
      :version     version
      :description "Related DBpedia resources to different media"
      :url         "https://www.github.com/allentiak/dbpedia-media-extractor"
      :scm         {:url "https://github.com/allentiak/dbpedia-media-extractor"}
      :license     {"GNU Affero General Public License (AGPL) 3 or later, with Clojure linking permission"
                   "https://www.gnu.org/licenses/agpl.html"}}
 jar {:main        'dbpedia-media-extractor.core
      :file        (str "dbpedia-media-extractor-" version "-standalone.jar")})

(deftask build
  "Build the project locally as a JAR."
  [d dir PATH #{str} "the set of directories to write to (target)."]
  (let [dir (if (seq dir) dir #{"target"})]
    (comp (aot) (pom) (uber) (jar) (target :dir dir))))

(deftask run
  "Run the project."
  [a args ARG [str] "the arguments for the application."]
  (require '[dbpedia-media-extractor.core :as app])
  (apply (resolve 'app/-main) args))

(require '[adzerk.boot-test :refer [test]])
