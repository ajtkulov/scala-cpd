import cpd.Some1
import org.scalatest.FunSuite

class ParseTest extends FunSuite {
  test("Normalize") {
    val source = Some1.parse("""object z {
                val z: Int= 1

}
                """)

    assert(Some1.normalize(source) == "objectz{valz:Int=1}")
  }

  test("Size") {
    val stat = Some1.parseStat("""
                val z = 1 + 2 + 3
                """)

    assert(Some1.size(stat) >= 5)
  }
}
