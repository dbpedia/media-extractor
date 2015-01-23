lazy val commonSettings = Seq(
  organization := "org.dbpedia.media_extractor",
  version := "0.0.1",
  scalaVersion := "2.11.5"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "media-extractor",
    libraryDependencies ++= Seq("ru.hh.oauth.subscribe" % "apis" % "3.0",
 	"org.scalatest" %% "scalatest" % "2.1.6" % "test",
	"org.glassfish.jersey.core" % "jersey-client" % "2.9",
	"org.scala-lang.modules" %% "scala-xml" % "1.0.2",
	"org.apache.jena" % "apache-jena-libs" % "2.11.2",
	"net.liftweb" %% "lift-json" % "2.6-RC1"
	)
   )
