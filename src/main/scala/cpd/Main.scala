package cpd

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox

object Main extends App {

  override def main(args: Array[String]): Unit = {
    val s =
      """
        |import scala.reflect.runtime.universe._
        |
        |// wr
        |object X { val z: Int = 1}
      """.stripMargin

    val tree = Apply(Select(Ident(TermName("x")), TermName("$plus")), List(Literal(Constant(2))))

    val tb = runtimeMirror(getClass.getClassLoader).mkToolBox()
    val q = showRaw(tb.parse(s))
    println(q)
  }
}
