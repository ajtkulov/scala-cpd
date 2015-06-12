package cpd

import scalariform.lexer._
import scalariform.parser._


object Main extends App {

  override def main(args: Array[String]): Unit = {
    val s =
      """package some
        |
        |import org.scala.test
        |
        | // test comment
        |object X {
        |  val z: Int = 1
        |
        |
        |}
      """.stripMargin
    val res = parseCompilationUnit(s)
    println(res)

    println()
    println(removeRedundancy(res))
  }

  private def parser(s: String) = new ScalaParser(ScalaLexer.tokenise(s).toArray)
  private def parseExpression(s: String) = parser(s).expr
  private def parseCompilationUnit(s: String) = parser(s).compilationUnit

  def removeRedundancy(ast: AstNode): AstNode = {
    ast match {
      case CompilationUnit(StatSeq(z,Some(PackageStat(_, _)), y), x) => CompilationUnit(StatSeq(z, None, removeRedundancy(y)), x)
      case x => x
    }
  }

  def removeRedundancy(list: List[(Token, Option[Stat])]): List[(Token, Option[Stat])] = {
    list.filter(x => {
      val res = x._2 match {
        case Some(ImportClause(_, _, _)) => true
        case _ => false
      }
      !res
    })
  }
}
