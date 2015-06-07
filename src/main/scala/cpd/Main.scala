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
  }

  private def parser(s: String) = new ScalaParser(ScalaLexer.tokenise(s).toArray)
  private def parseExpression(s: String) = parser(s).expr
  private def parseCompilationUnit(s: String) = parser(s).compilationUnit
}
