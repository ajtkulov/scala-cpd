package cpd

import sbt._
import Keys._
import sbt.complete.Parser
import sbt.complete.Parsers._

object CpdPlugin extends AutoPlugin {

  object autoImport {
    val cpd = inputKey[Unit]("Copy-paste detector")
  }

  import autoImport._

  private val argsParser: Parser[Seq[String]] = (Space ~> StringBasic).*

  override def projectSettings: Seq[Setting[_]] = Seq(
    cpdSetting)

  def cpdSetting: Setting[_] = cpd := {

    val args: Seq[String] = argsParser.parsed

    val sourceIdx: Int = args.indexOf("--source")

    val path = if (sourceIdx >= 0) {
      args(sourceIdx + 1)
    } else {
      (sourceDirectory in Compile).value.getAbsolutePath
    }

    val errorLevelIdx = args.indexOf("--errorLevel")

    val errorLevel = if (errorLevelIdx >= 0) {
      args(errorLevelIdx + 1).toInt
    } else {
      10
    }

    Main.handle((sourceDirectory in Compile).value.getAbsolutePath, errorLevel, (target in Compile).value.getAbsolutePath)

    val log = streams.value.log
    log.debug(s"args = $args")

    ()
  }
}