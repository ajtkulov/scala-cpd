package cpd

import cpd.Model._

import scala.collection.mutable.ListBuffer

object LongestCommonSubstring extends WellKnownSubstring {
  def longestCommonSubstring(values : StringStatisticSet, minFrequency : Int = 1, leftBorder : Int = 1, rightBorder : Int = Int.MaxValue) : Option[String] = {
    var left : Int = leftBorder
    var right : Int = Math.min(rightBorder, values.values.map(x => x._1.length).max)

    while (left < right) {
      val mid = (right + left + 1) / 2
      val intermediateResult: Option[String] = find(values, mid, minFrequency)
      if (!intermediateResult.isDefined) {
        right = mid - 1
      } else {
        left = mid
      }
    }

    find(values, left, minFrequency)
  }

  def find(values : StringStatisticSet, length : Int, minFrequency : Int) : Option[String] = {
    val context = newContext()
    for (struct <- values.values) {
      hash(struct, length, context)
    }

    val best: (ListBuffer[(Id, Int)], Int) = context.getBestPositions(minFrequency)

    if (best._2 > 0) {
      val shift: Int = best._1(0)._2
      val id: Id = best._1(0)._1
      Some(values.getStringById(id).substring(shift, shift + length))
    } else {
      None
    }
  }
}
