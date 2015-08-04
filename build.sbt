sbtPlugin := true

name := "scala-cpd"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

parallelExecution in Test := false

fork := true
