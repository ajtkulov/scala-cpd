package cpd

import scala.collection.mutable.ListBuffer

object StringUtils {
  def split(str : String, pattern : String) : Array[String] = {
    val res = ListBuffer[String]()
    var cur = str
    var pos = cur.indexOf(pattern)
    while (pos != -1) {
      if (pos > 0) {
        res.append(cur.substring(0, pos))
      }
      cur = cur.substring(pos + pattern.length)
      pos = cur.indexOf(pattern)
    }

    if (cur.nonEmpty) {
      res.append(cur)
    }

    res.toArray
  }
}
