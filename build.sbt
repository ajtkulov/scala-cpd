//sbtPlugin := true

organization := "com.github.ajtkulov"

name := "scala-cpd"

version := "0.3"

scalaVersion := "2.11.7"

libraryDependencies += "org.scalameta" %% "scalameta" % "1.0.0"

parallelExecution in Test := false

fork := true
