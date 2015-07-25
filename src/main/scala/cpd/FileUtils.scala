package cpd

import java.io.File
import scala.io.Source

object FileUtils {
  type FileName = String
  type Dir = String

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

  def fromFile(filePath : FileName) : Iterator[String] = scala.io.Source.fromFile(filePath, "iso-8859-1").getLines
  def fromFileAsString(filePath : FileName) : String = fromFile(filePath).mkString("\n")
}
