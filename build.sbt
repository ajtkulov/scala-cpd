sbtPlugin := true

organization := "ajtkulov"

name := "scala-cpd"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

parallelExecution in Test := false

fork := true


val myProject = (project in file(".")).enablePlugins(CommandPlugin)

