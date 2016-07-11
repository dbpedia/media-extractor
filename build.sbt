lazy val commonSettings = Seq(
  organization := "org.dbpedia",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "media-extractor",
    libraryDependencies ++= Seq(
      "com.github.scribejava" % "scribejava-apis" % "2.8.1",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test",
      "org.scalacheck" %% "scalacheck" % "1.13.0" % "test"
    )
  )
