import AssemblyKeys._

name := "nuke"

scalaVersion := "2.9.1"

seq(assemblySettings: _*)

mainClass in assembly := Some("sky.sns.nuke.NukeApp")

resolvers ++= Seq(
  "Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Java.net" at "http://download.java.net/maven/2/",
  "SNS Releases" at "http://repo.sns.sky.com:8081/artifactory/libs-releases/"
)

libraryDependencies ++= Seq(
  "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
  "org.scala-tools.time" %% "time" % "0.5",
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default"
)