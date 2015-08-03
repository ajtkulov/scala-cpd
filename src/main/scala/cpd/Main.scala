package cpd

import cpd.ExprType.ExprType

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox

object Main extends App {

  override def main(args: Array[String]): Unit = {
    val tree = parse(FileUtils.fromFileAsString("src/main/resources/1.scala"))
    val tree1 = parse(FileUtils.fromFileAsString("src/main/resources/2.scala"))

    val traverser = new Traverse(tree, "1.scala")
    val traverser1 = new Traverse(tree1, "2.scala")

    val res: Map[(String, ExprType), (String, String, Int)] = traverser.cache.intersect(traverser1.cache)
    println(res.mkString("\n"))
  }

  class Traverse(tree: Tree, fileName: String) extends Traverser {
    val cache = new Cache()

    traverse(tree)

    override def traverse(tree: Tree): Unit = tree match {
      case app @ Apply(fun, args) =>
        cache.addExpr(app, ExprType.Apply, fileName)
        super.traverse(fun)
        super.traverseTrees(args)
      case def1 @ DefDef(mod, term, types, vals, tree1, tree2) =>
        cache.addExpr(def1, ExprType.Def, fileName)
        super.traverse(tree1)
        super.traverse(tree2)
      case val1 @ ValDef(mod, term, tree1, tree2) =>
        cache.addExpr(val1, ExprType.Val, fileName)
        super.traverse(tree1)
        super.traverse(tree2)
      case fun @ Function(params, tree) =>
        cache.addExpr(fun, ExprType.Fun, fileName)
        super.traverse(tree)
      case block @ Block(stat, tree) =>
        cache.addExpr(block, ExprType.Block, fileName)
        super.traverseTrees(stat)
        super.traverse(tree)
      case if1 @ If(cond, then1, else1) =>
        cache.addExpr(if1, ExprType.If, fileName)
        super.traverse(cond)
        super.traverse(then1)
        super.traverse(else1)
      case match1 @ Match(selector, cases) =>
        cache.addExpr(match1, ExprType.Match, fileName)
        super.traverse(selector)
        super.traverseTrees(cases)
      case case1 @ CaseDef(pat, guard, body) =>
        cache.addExpr(case1, ExprType.Case, fileName)
        super.traverse(pat)
        super.traverse(guard)
        super.traverse(body)

      case _ => super.traverse(tree)
    }
  }

  class TreeSize(tree: Tree) extends Traverser {
    var _size: Int = 0

    def size: Int = _size

    traverse(tree)

    override def traverse(tree: Tree): Unit = tree match {
      case Ident(_) =>
        _size += 1
      case TermName(_) =>
        _size += 1
      case Literal(_) =>
        _size += 1

      case _ => super.traverse(tree)
    }
  }

  def parse(source: String): Tree = {
    val s = source.split("\n").filter(x => !x.startsWith("package ")).mkString("\n")
    val tb = runtimeMirror(getClass.getClassLoader).mkToolBox()
    tb.parse(s)
  }
}

