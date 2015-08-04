package cpd

import cpd.ExprType.ExprType
import cpd.Main.TreeSize

import scala.collection.mutable
import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox

object ExprType extends Enumeration {
  type ExprType = Value

  val Apply = Value("Apply")
  val Block = Value("Block")
  val Def = Value("Def")
  val Val = Value("Val")
  val Fun = Value("Fun")
  val If = Value("If")
  val Match = Value("Match")
  val Case = Value("Case")
}

case class Value1(fileName: String, tree: Tree, size: Int) {}

class Cache() {
  private val _result = scala.collection.mutable.Map[(String, ExprType), (String, String, Int)]()

  def result: Map[(String, ExprType), (String, String, Int)] = _result.toMap

  private val map = scala.collection.mutable.Map[(String, ExprType), Value1]()
  def addExpr(tree: Tree, treeType: ExprType, fileName: String): Unit = {
    val size = new TreeSize(tree).size

    val key: (String, ExprType) = (tree.toString, treeType)
    if (map.contains(key)) {
      _result.put(key, (map(key).fileName, fileName, size))
    } else {
      map.put(key, Value1(fileName, tree, size))
    }
  }

  def intersect(other: Cache): Map[(String, ExprType), (String, String, Int)] = {
    other.map.filter(x => map.contains(x._1)).map(x => (x._1, (x._2.fileName, map(x._1).fileName, x._2.size))).toMap
  }
}

