package cpd

import java.io.{PrintWriter, File}
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

  def isFileExist(fileName: FileName): Boolean = {
    new java.io.File(fileName).exists
  }

  private def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def fromFile(filePath : FileName) : Iterator[String] = scala.io.Source.fromFile(filePath, "iso-8859-1").getLines
  def fromFileAsString(filePath : FileName) : String = fromFile(filePath).mkString("\n")

  def write(fileName : FileName, iterator : Iterator[String]) : Unit = {
    withFile(fileName) { output =>
      iterator.foreach(line => output.println(line))
    }
  }

  def withFile[A](fileName : FileName)(func : PrintWriter => A) : Unit = {
    val file = new File(fileName)
    val write = new PrintWriter(file)
    try {
      func(write)
    }
    finally {
      write.close()
    }
  }
}
