package cpd

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox

object Main extends App {

  override def main(args: Array[String]): Unit = {
    val tree = parse(FileUtils.fromFileAsString("src/main/resources/1.scala"))
    val tree1 = parse(FileUtils.fromFileAsString("src/main/resources/2.scala"))

    val traverser = new Traverse(tree)
    val traverser1 = new Traverse(tree1)

    val inter = traverser1.blocks.toSeq.map(x => x.toString()).intersect(traverser.blocks.toSeq.map(x => x.toString()))
    println(inter.mkString("\n\n"))
  }

  class Traverse(tree: Tree) extends Traverser {
    var applies = List[Apply]()
    var defs = List[DefDef]()
    var vals = List[ValDef]()
    var funs = List[Function]()
    var blocks = List[Block]()

    traverse(tree)

    override def traverse(tree: Tree): Unit = tree match {
      case app @ Apply(fun, args) =>
        applies = app :: applies
        super.traverse(fun)
        super.traverseTrees(args)
      case def1 @ DefDef(mod, term, types, vals, tree1, tree2) =>
        defs = defs.::(def1)
        super.traverse(tree1)
        super.traverse(tree2)
      case val1 @ ValDef(mod, term, tree1, tree2) =>
        vals = vals.::(val1)
        super.traverse(tree1)
        super.traverse(tree2)
      case fun @ Function(params, tree) =>
        funs = funs.::(fun)
        super.traverse(tree)
      case block @ Block(stat, tree) =>
        blocks = blocks.::(block)
        super.traverseTrees(stat)
        super.traverse(tree)

      case _ => super.traverse(tree)
    }
  }

  def parse(source: String): Tree = {
    val s = source.split("\n").filter(x => !x.startsWith("package ")).mkString("\n")
    val tb = runtimeMirror(getClass.getClassLoader).mkToolBox()
    tb.parse(s)
  }
}

