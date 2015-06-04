package cpd


import cpd.Model._
import scala.collection.mutable.ListBuffer

trait WellKnownSubstring extends RabinKarpMatching {
  type Position = (Id, Int)
  type Input = (String, Frequency, Id)

  def wellKnownSubstring(values : StringStatisticSet, leftBorder : Int = 1, rightBorder : Int = Int.MaxValue) : String = {
    var bestLength : Int = 0
    var bestPosition : Position = (0, 0)
    var maxEffect : Int = 0

    val maxLength: Int = values.values.maxBy(x => x._1.length)._1.length

    val right: Int = Math.min(rightBorder, maxLength)

    for (len <- leftBorder to right) {
      val context = newContext()
      for (struct <- values.values) {
        hash(struct, len, context)
      }

      val max: (ListBuffer[(Id, Int)], Int) = context.getBestPositions()

      if (max._2 * len > maxEffect) {
        maxEffect = max._2 * len
        bestLength = len
        bestPosition = max._1(0)
      }
    }

    values.getStringById(bestPosition._1).substring(bestPosition._2, bestPosition._2 + bestLength)
  }

  def getString(input: Input): String = input._1

  def getProfit(input: Input): Int = input._2

  override def getPosition(input: Input, shift: Int): Position = (input._3, shift)
}

object WellKnownSubstring extends WellKnownSubstring {}