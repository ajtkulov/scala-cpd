package cpd

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox

object Main extends App {

  override def main(args: Array[String]): Unit = {
    val tree = parse(FileUtils.fromFileAsString("src/main/resources/1.scala"))
    traverser.traverse(tree)

    println(traverser.applies.mkString("\n"))
  }

  object traverser extends Traverser {
    var applies = List[Apply]()

    override def traverse(tree: Tree): Unit = tree match {
      case app @ Apply(fun, args) =>
        applies = app :: applies
        super.traverse(fun)
        super.traverseTrees(args)

      case _ => super.traverse(tree)
    }
  }

  def parse(source: String): Tree = {
    val s = source.split("\n").filter(x => !x.startsWith("package ")).mkString("\n")
    val tb = runtimeMirror(getClass.getClassLoader).mkToolBox()
    tb.parse(s)
  }
}

