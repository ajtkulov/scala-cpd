package cpd

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

trait RabinKarpMatching {
  type HashValue = Long
  type Position
  type Input
  type PositionTable = scala.collection.mutable.Map[HashValue, scala.collection.mutable.ListBuffer[Position]]
  type CountTable = scala.collection.mutable.Map[HashValue, Int]
  lazy val base : Int = 31

  case class Context(countTable : CountTable, positionTable : PositionTable) {
    def add(hashValue : HashValue, position : Position, profit : Int = 1) : Unit = {
      if (countTable.contains(hashValue)) {
        countTable(hashValue) += profit
        positionTable(hashValue).append(position)
      } else {
        countTable(hashValue) = profit
        positionTable(hashValue) = scala.collection.mutable.ListBuffer[Position](position)
      }
    }

    def getBestPositions(minFrequency : Int = 1) : (ListBuffer[Position], Int) = {
      val ar: Array[(HashValue, Int)] = countTable.toArray.sortBy(x => x._2).reverse
      var i = 0
      var find = false
      while (i < ar.length && !find) {
        if (positionTable(ar(i)._1).length > minFrequency) {
          find = true
        } else {
          i = i + 1
        }
      }

      if (find) {
        (positionTable(ar(i)._1), countTable(ar(i)._1))
      } else {
        (ListBuffer(), 0)
      }
    }
  }

  def newContext() : Context = Context(mutable.Map[HashValue, Int](), mutable.Map[HashValue, ListBuffer[Position]]())

  def getString(input : Input) : String
  def getProfit(input : Input) : Int
  def getPosition(input : Input, shift : Int) : Position

  def hash(input : Input, len : Int, context : Context) : Context = {
    val str : String = getString(input)
    val profit : Int = getProfit(input)
    if (str.length < len) {
      context
    } else {
      var curHash : HashValue = 0
      var curPower : HashValue = 1
      for (i <- len - 1 to 0 by -1) {
        curHash += str.charAt(i) * curPower
        if (i > 0) {
          curPower *= base
        }
      }

      context.add(curHash, getPosition(input, 0), profit)

      for (i <- len to str.length - 1) {
        curHash = (curHash - str.charAt(i - len) * curPower) * base + str.charAt(i)
        context.add(curHash, getPosition(input, i - len + 1), profit)
      }

      context
    }
  }
}

object RabinKarpMatching extends RabinKarpMatching {
  type Position = Int
  type Input = String

  def getString(input : Input) : String = input
  def getProfit(input : Input) : Int = 1

  def longestCommonSubstring(str : String, leftBorder : Int = 1, rightBorder : Int = Int.MaxValue) : String = {
    var bestLength : Int = 0
    var bestShift : Int = 0

    for (len <- leftBorder to Math.min(str.length, rightBorder)) {
      val context = newContext()
      hash(str, len, context)
      val max: Array[(RabinKarpMatching.HashValue, ListBuffer[Position])] = context.positionTable.filter(x => x._2.length > 1).toArray.take(1)

      if (max.length > 0) {
        bestLength = len
        bestShift = max(0)._2(0)
      }
    }

    str.substring(bestShift, bestShift + bestLength)
  }

  def getPosition(input: Input, shift: Int): Position = shift
}