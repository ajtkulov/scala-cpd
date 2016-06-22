package cpd


import scala.meta._
import scala.meta.tokens.Token.Space


object Some1 {
  val z1: Parsed[Source] =
    """ class some1(value: Int) {
          for (i <- 1 to 10) yield     { println(123)
          }
      }

         object Some {
           val z = 123 + 34

           {
             println(1234)
             println(12345)
             println(12346)
           }

         }
    """.parse[Source]

  z1.get.stats.head.traverse {
    //      case a: scala.meta.Decl => println(1235345)
    //      case a: scala.meta.Defn.Class => println(a); println("class")
    case a: scala.meta.Term.For => println(a); println("*for")
    case a: scala.meta.Term.ForYield => println(a); println("*for yield")
    case a: scala.meta.Defn.Class => println(a); println("*class"); println(a.tokens.filterNot(x => x.is[Token.Space] || x.is[Token.Tab]) mkString(", "));
    case a: scala.meta.Defn.Val => println(a); println("*val")
    case a: scala.meta.Defn.Def => println(a); println("*def")
    case a: scala.meta.Defn => println(a); println("*defn")
    case a: scala.meta.Stat if (a.children.length > 1) => println(a); println(s"*statsssss: ${a.children.length}");  println(a.children.mkString("[", ", ", "]"))
    case a: scala.meta.Stat => println(a); println("*stat");

    //      case a: Tree => println(a)
    case _ =>
  }
}