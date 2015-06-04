package cpd

import java.io.File
import scala.io.Source

object FileUtils {
  def readFile(fileName : String) : String = {
    Source.fromFile(fileName).getLines().mkString("\n")
  }

  def filesInDirectory(dirName : String) : Array[File] = {
    recursiveListFiles(new File(dirName))
  }

  private def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }
}
