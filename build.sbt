lazy val commonSettings = Seq(
  organization := "org.dbpedia.media_extractor",
  version := "0.0.1",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "media-extractor",
    libraryDependencies ++= Seq(
	"com.github.scribejava" % "scribejava-apis" % "2.0.1",
 	"org.scalatest" %% "scalatest" % "2.2.4" % "test"
    )
  )
