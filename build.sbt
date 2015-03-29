name := "scala-cpd"

version := "1.0"

scalaVersion := "2.11.4"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

parallelExecution in Test := false

//scalacOptions ++= Seq("-deprecation", "-feature")

scalacOptions in compile ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint", "-feature")


libraryDependencies ++= Seq(
//  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.typesafe" % "config" % "1.2.1" % "optional",
      "javax.inject" % "javax.inject" % "1")

fork := true

//scalaHome := Some(file("/opt/local/scala-2.11.1/"))

//libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value


libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value % "test" // for ToolBox

