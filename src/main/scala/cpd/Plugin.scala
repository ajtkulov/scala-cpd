package cpd

import sbt._
import Keys._
import sbt.complete.Parsers._

import java.net.URL

import scala.util.control.NonFatal

object CommandPlugin extends AutoPlugin {

  object autoImport {
    val rssList = settingKey[Seq[String]]("The list of RSS urls to update.")
    val rss = inputKey[Unit]("Prints RSS")
  }

  import autoImport._

  private val argsParser = (Space ~> StringBasic).*

  override def projectSettings: Seq[Setting[_]] = Seq(
    rssSetting
  )

  def rssSetting: Setting[_] = rss := {
    // Parse the input string into space-delimited strings.
    val args = argsParser.parsed
    // Sbt provided logger.
    val log = streams.value.log
    log.debug(s"args = $args")

    ()
  }
}