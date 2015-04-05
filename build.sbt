lazy val root = (project in file(".")).
  settings(
    name := """pdf-merge-service""",
    version := "1.0",
    scalaVersion := "2.11.6"
  )

resolvers += Resolver.bintrayRepo("dwhjames", "maven")

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

libraryDependencies ++= Seq(
  "org.apache.pdfbox" % "pdfbox" % "1.8.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-core" % "1.1.3",
  "org.slf4j" % "slf4j-api" % "1.7.12",
  "org.slf4j" % "slf4j-simple" % "1.7.12",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.4",
  "commons-io" % "commons-io" % "2.4",
  "com.lihaoyi" %% "scalatags_sjs0.6" % "0.5.1" intransitive(),
  "org.scala-js" %% "scalajs-library" % "0.6.2" intransitive(),
  "com.github.finagle" %% "finch-core" % "0.6.0",
  "com.github.finagle" %% "finch-jackson" % "0.6.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

import de.johoop.findbugs4sbt.FindBugs._

findbugsSettings

testOptions in Test += Tests.Argument("-oD")

scalacOptions ++= Seq("-deprecation", "-Xfatal-warnings", "-Xlint", "-feature")

dependencyOverrides += "org.scala-lang" %% "scala-library" % "2.11.6"

dependencyOverrides += "org.scala-js" % "scalajs-library_2.11" % "0.6.2"

packageArchetype.java_application
