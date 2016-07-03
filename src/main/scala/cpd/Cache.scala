package cpd

import scala.meta._


object Some1 {
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

  def traverse(source: Source): Unit = {
    source.stats.foreach(stat =>
      stat.traverse {
        case a: scala.meta.Term.For => println(a); println("*for")
        case a: scala.meta.Term.ForYield => println(a); println("*for yield")
        case a: scala.meta.Defn.Class => println(a); println("*class"); println(normalize(a));
        case a: scala.meta.Defn.Val => println(a); println("*val")
        case a: scala.meta.Defn.Def => println(a); println("*def")
        case a: scala.meta.Defn => println(a); println("*defn")
        case a: scala.meta.Stat if (a.children.length > 1) => println(a); println(s"*statsssss: ${a.children.length}"); println(a.children.mkString("[", ", ", "]"))
        case a: scala.meta.Stat => println(a); println("*stat");
        case _ =>
      })

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