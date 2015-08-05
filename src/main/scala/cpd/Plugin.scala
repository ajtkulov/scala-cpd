package cpd

import sbt._
import Keys._
import sbt.complete.Parser
import sbt.complete.Parsers._

object CommandPlugin extends AutoPlugin {

  object autoImport {
    val cpd = inputKey[Unit]("Copy-paste detector")
  }

  import autoImport._

  private val argsParser: Parser[Seq[String]] = (Space ~> StringBasic).*

  override def projectSettings: Seq[Setting[_]] = Seq(
    rssSetting)

  def rssSetting: Setting[_] = cpd := {

    val args: Seq[String] = argsParser.parsed

    val path =  args.headOption.getOrElse(s"${new File(".").getAbsolutePath.dropRight(2)}/src/main")

    Main.main(Array[String](path))

    val log = streams.value.log
    log.debug(s"args = $args")

    ()
  }
}