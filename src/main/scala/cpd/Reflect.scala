package cpd

import scala.tools.reflect.ToolBox


object Reflect {
  def Some(s : String) = {
    val tb = scala.reflect.runtime.currentMirror.mkToolBox()
    val tree = tb.parse("1 to 3 map (_+1)")
    println(tree)
  }
}
