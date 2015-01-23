name := "media-extractor"

version := "0.0.1"

scalaVersion := "2.11.5"

resolvers += Resolver.mavenLocal

libraryDependencies += "ru.hh.oauth.subscribe" % "apis" % "3.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

libraryDependencies += "org.glassfish.jersey.core" % "jersey-client" % "2.9"

libraryDependencies += "org.scribe" % "scribe" % "1.3.7" 

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.2"

libraryDependencies += "org.apache.jena" % "apache-jena-libs" % "2.11.2"

libraryDependencies += "net.liftweb" %% "lift-json" % "2.6-RC1"

