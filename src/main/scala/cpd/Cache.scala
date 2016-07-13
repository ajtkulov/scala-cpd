package cpd

import cpd.Some1._

import scala.collection.immutable.Seq
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.meta.Term.Block
import scala.meta._

case class SourceFile(path: Path) {
  lazy val source: Source = Some1.parse(FileUtils.readFile(path))
  lazy val subTrees = Some1.traverse(source)
  lazy val strings: List[(NormalizedSource, Tree)] = subTrees.map(x => (Some1.normalize(x), x))
}

case class ResultItemPart(source: Path, tree: Tree) {}

case class ResultItem(fst: ResultItemPart, snd: ResultItemPart, source: NormalizedSource) {}

case class Project(files: List[Path]) {
  def handle(): List[ResultItem] = {

    val res = ArrayBuffer[ResultItem]()
    val global: mutable.Map[String, (Tree, Path)] = scala.collection.mutable.Map[String, (Tree, Path)]()
    for (path <- files; item <- SourceFile(path).strings) {
      val occ: Option[(Tree, Path)] = global.put(item._1, (item._2, path))
      if (occ.isDefined) {
        val prev: (Tree, Path) = occ.get
        res.append(ResultItem(ResultItemPart(path, item._2), ResultItemPart(prev._2, item._2), item._1))
      }
    }

    res.toList
  }
}

object Some1 {
  type Path = String
  type NormalizedSource = String
  lazy val minSize: Int = 10

  def normalize(tree: Tree): String = {
    tree.tokens.filterNot(x => x.is[Token.Space] || x.is[Token.Tab] || x.is[Token.CR] || x.is[Token.LF]).mkString("")
  }

  def parse(source: String): Source = {
    source.parse[Source].get
  }

  def parseTerm(source: String): Term = {
    source.parse[Term].get
  }

  def parseStat(source: String): Stat = {
    source.parse[Stat].get
  }

  def traverse(source: Source): List[Tree] = {
    val buffer = ArrayBuffer[Tree]()

    def addTree(tree: Tree): Unit = {
      if (size(tree) > minSize) {
        buffer.append(tree)
      }
    }

    source.stats.foreach(stat =>
      if (size(stat) > minSize) {
        stat.traverse {
          case a: scala.meta.Term.Block => {
            addTree(a)
            val stats = a.stats

            for (i <- 0 until stats.size - 1; j <- i + 1 to stats.size) {
              val sub: Seq[Stat] = stats.slice(i, j)
              addTree(Block(sub))
            }
          }
          case a: scala.meta.Term.For => addTree(a)
          case a: scala.meta.Term.ForYield => addTree(a)
          case a: scala.meta.Defn.Class => addTree(a)
          case a: scala.meta.Defn.Val => addTree(a)
          case a: scala.meta.Defn.Def => addTree(a)
          case a: scala.meta.Defn => addTree(a)
          case a: scala.meta.Stat => addTree(a)
          case _ =>
        }
      })
    buffer.toList
  }

  def size(values: Seq[Tree]): Int = {
    values.map(x => size(x)).sum
  }

  def size(stat: Tree): Int = {
    var res = 0
    stat.traverse {
      case a: scala.meta.Term.For => res = res + 1
      case a: scala.meta.Term.ForYield => res = res + 1
      case a: scala.meta.Defn.Class => res = res + 1
      case a: scala.meta.Defn.Val => res = res + 1
      case a: scala.meta.Defn.Def => res = res + 1
      case a: scala.meta.Defn => res = res + 1
      case a: scala.meta.Stat => res = res + 1
      case _ =>
    }

    res
  }
}
