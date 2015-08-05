package cpd

import sbt._
import Keys._
import sbt.complete.Parser
import sbt.complete.Parsers._

object CommandPlugin extends AutoPlugin {

  object autoImport {
    val cpd = inputKey[Unit]("Prints RSS")
  }

  import autoImport._

  private val argsParser: Parser[Seq[String]] = (Space ~> StringBasic).*

  override def projectSettings: Seq[Setting[_]] = Seq(
    rssSetting)

  def rssSetting: Setting[_] = cpd := {
    val args: Seq[String] = argsParser.parsed

    println(args.mkString(" "))
    val log = streams.value.log
    log.debug(s"args = $args")

    ()
  }
}