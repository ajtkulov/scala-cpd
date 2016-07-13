import cpd.Some1
import org.scalatest.FunSuite

import scala.meta.Tree

class ParseTest extends FunSuite {
  test("Normalize") {
    val source = Some1.parse(
      """object z {
                val z: Int= 1

}
      """)

    assert(Some1.normalize(source) == "objectz{valz:Int=1}")
  }

  test("Size") {
    val stat = Some1.parseStat(
      """
                val z = 1 + 2 + 3
      """)

    assert(Some1.size(stat) >= 5)
  }

  test("SubBlock") {
    val source = Some1.parse(
      """object z {
                {
                println(1)
                println(2)
                println(3)
                println(4)
                println(5)
                println(6)
                println(7)
                println(8)
                println(9)
                }
      }
      """)
    val traverse: List[Tree] = Some1.traverse(source)
    val normalized: List[String] = traverse.map(x => Some1.normalize(x))

    assert(normalized.contains("{println(1)println(2)println(3)println(4)println(5)}"))
    assert(normalized.contains("{println(5)println(6)println(7)println(8)println(9)}"))
    assert(!normalized.contains("{println(5)println(6)}"))
  }
}
