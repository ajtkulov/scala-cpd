package cpd

import cpd.ExprType.ExprType

import scala.collection.immutable.Seq
import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox
import scala.xml.XML

object Main extends App {
  override def main(args: Array[String]): Unit = {
  }

  def normalizeCode(code: String): String = {
    code.filterNot(x => "\r\t\n ".contains(x))
  }

  def readExceptionInstances(fileName: String): Seq[String] = {
    (XML.loadFile(fileName) \\ "except").map(x => x.text).map(x => normalizeCode(x))
  }

  def handle(sourceDir: String, errorLevel: Int, outputFileName: String, exceptFile: Option[String] = None): Unit = {
    val files = FileUtils.filesInDirectory(sourceDir).map(x => x.getAbsolutePath).filter(x => x.endsWith(".scala"))

    val traverse = new Traverse()

    files.foreach { fileName =>
      val parsed = parse(FileUtils.fromFileAsString(fileName))
      if (parsed.isSuccess) {
        traverse.addFile(parsed.get, fileName)
      }
    }

    val exceptSet: Set[String] = exceptFile.map(x => readExceptionInstances(x)).getOrElse(Nil).toSet

    val res: Map[(String, ExprType), (String, String, Int)] = traverse.cache.result

    val filter = Map[ExprType, Int]().withDefaultValue(errorLevel)

    val filtered: List[((String, ExprType), (String, String, Int))] = res.filter(x => filter(x._1._2) <= x._2._3).toList.filterNot(x => exceptSet.contains(normalizeCode(x._1._1)))

    val xml =
      <cpd>
        { for (item <- filtered) yield <item errorWeight={ item._2._3.toString } file1={ item._2._1 } file2={ item._2._2 } type={ item._1._2.toString }>
          <code>
            { scala.xml.PCData(item._1._1) }
          </code>
        </item> }
      </cpd>
    val printer = new scala.xml.PrettyPrinter(800, 2)

    FileUtils.write(outputFileName, Iterator.single(printer.format(xml)))
  }

  class Traverse() extends Traverser {
    val cache = new Cache()

    var _fileName: String = _
    def addFile(tree: Tree, fileName: String): Unit = {
      _fileName = fileName
      traverse(tree)
    }

    override def traverse(tree: Tree): Unit = tree match {
      case app @ Apply(fun, args) =>
        cache.addExpr(app, ExprType.Apply, _fileName)
        super.traverse(fun)
        super.traverseTrees(args)
      case def1 @ DefDef(mod, term, types, vals, tree1, tree2) =>
        cache.addExpr(def1, ExprType.Def, _fileName)
        super.traverse(tree1)
        super.traverse(tree2)
      case val1 @ ValDef(mod, term, tree1, tree2) =>
        cache.addExpr(val1, ExprType.Val, _fileName)
        super.traverse(tree1)
        super.traverse(tree2)
      case fun @ Function(params, tree) =>
        cache.addExpr(fun, ExprType.Fun, _fileName)
        super.traverse(tree)
      case block @ Block(stat, tree) =>
        cache.addExpr(block, ExprType.Block, _fileName)
        super.traverseTrees(stat)
        super.traverse(tree)
      case if1 @ If(cond, then1, else1) =>
        cache.addExpr(if1, ExprType.If, _fileName)
        super.traverse(cond)
        super.traverse(then1)
        super.traverse(else1)
      case match1 @ Match(selector, cases) =>
        cache.addExpr(match1, ExprType.Match, _fileName)
        super.traverse(selector)
        super.traverseTrees(cases)
      case case1 @ CaseDef(pat, guard, body) =>
        cache.addExpr(case1, ExprType.Case, _fileName)
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
      //      case TermName(_) =>
      //        _size += 1
      case Literal(_) =>
        _size += 1

      case _ => super.traverse(tree)
    }
  }

  def parse(source: String): scala.util.Try[Tree] = {
    scala.util.Try {
      val s = source.split("\n").filter(x => !x.startsWith("package ")).mkString("\n")
      val tb = runtimeMirror(getClass.getClassLoader).mkToolBox()
      tb.parse(s)
    }
  }
}

