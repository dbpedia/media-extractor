name := "media-extractor"

version := "0.0.1"

scalaVersion := "2.11.0"

// scribe repo
resolvers += "scribe-java-mvn-repo" at "https://raw.github.com/fernandezpablo85/scribe-java/mvn-repo/"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

libraryDependencies += "org.glassfish.jersey.core" % "jersey-client" % "2.9"

libraryDependencies += "org.scribe" % "scribe" % "1.3.6" 

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.2"
