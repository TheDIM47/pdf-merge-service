packageArchetype.java_application

name := """pdf-merge-service"""

version := "1.0"

scalaVersion := "2.11.6"

resolvers += Resolver.bintrayRepo("dwhjames", "maven")

libraryDependencies ++= Seq(
  "org.apache.pdfbox" % "pdfbox" % "1.8.8",

  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-core" % "1.1.2",

  "org.slf4j" % "slf4j-api" % "1.7.10",
  "org.slf4j" % "slf4j-simple" % "1.7.10",

  "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.1",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.1",

  "commons-io" % "commons-io" % "2.4",

  "com.twitter" %% "finagle-core" % "6.24.0",
  "com.twitter" %% "finagle-httpx" % "6.24.0",
  "com.github.finagle" %% "finch-core" % "0.5.0",
  "com.github.finagle" %% "finch-jackson" % "0.5.0",

  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

testOptions in Test += Tests.Argument("-oD")
