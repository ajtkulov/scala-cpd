import cpd.{Project, Some1, SourceFile}
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

//  test("Some") {
////    val source = SourceFile("/Users/pavel/code/scala-cpd/src/test/scala/ParseTest.scala")
//    val source = SourceFile("/Users/pavel/tmp/1.scala")
//    val strings: List[String] = source.subTrees.map(x => Some1.normalize(x))
//    strings.foreach(println)
//  }

  test("Project") {
    val project = Project(List("/Users/pavel/tmp/1.scala"))
    val res = project.handle()
    println(res.mkString("\n"))
  }
}
