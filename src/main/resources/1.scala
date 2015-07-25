package reports.spark.segment

import java.net.URLDecoder

import ai.annotation.JsonCreated
import enums.RefererSegment
import infrastructure.transform.StringLineTransforming
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import reports.ReportTypes._
import reports.spark.{PeriodHdfsReport, PageviewsBasedSparkReport}

case class SegmentTagsReportItem(tags: Seq[Tag], date: IntDate, browserId: BrowserId, segment: RefererSegment, url: Url)

trait SegmentTagsReport extends PageviewsBasedSparkReport {
  type Report = Array[(Tag, Long)]
  type InputType = SegmentTagsReportItem

  override def report(rdd: RDD[InputType]): Report = {
    val queryContainsNypost : String => Boolean = (query : String) =>
      query.contains("new york post") || query.contains("ny post") || query.contains("nypost")
    val getSearchQuery : Url => String = (url : Url) => {
      val split = url.split(Array('&', '|', '?'))
      val fil = split.filter(x => x.startsWith("q=") || x.startsWith("p="))
      if (fil.isEmpty) {
        ""
      } else {
        val query = fil(0).substring(2)

        try {
          val res = URLDecoder.decode(query).toLowerCase
          res.replace("\t", "").replace("\n", "").replace("\r", "")
        } catch {
          case e : Exception => ""
        }
      }
    }

    rdd.keyBy(x => x.browserId).groupByKey(groupByKeyPartitionCountBigData)
      // filter by daysAttended = 3
      .map(x => (x._2, x._2.groupBy(y => y.date)))
      .filter(x => x._2.size >= 3
      || (
      x._1.size >= 3
        && (
          x._1.exists(y => y.segment == RefererSegment.Direct.id)
          ||
          x._1.exists(y => y.segment == RefererSegment.Search.id && queryContainsNypost(getSearchQuery(y.url)))
          )
        )
      )
      // count pageviews grouping by tag
      .flatMap(x => x._1.flatMap(y => y.tags.filter(z => z.startsWith("/"))))
      .countByValue().toArray
  }

  override def prepareInputRDD(inputRDD: RDD[StringLineTransforming]): RDD[InputType] = {
    inputRDD.filter(x => x.contentAuthor.nonEmpty)
      .map(x => SegmentTagsReportItem(x.tags, x.date, x.browserId, x.refererSegmentId, x.rawReferer))
  }

  override def output(report: Report): Iterator[String] = {
    report
      .toArray
      .sortBy(x => x._2).reverse
      .toIterator
      .map(x => s"${x._1}\t${x._2}")
  }

  override val name: String = "SegmentTagsReport"
}

@JsonCreated("SegmentTagsReport")
case class SegmentTagsHdfsReport(appId : String, beginDate : DateTime, endDate : DateTime) extends PeriodHdfsReport(appId, beginDate, endDate) with SegmentTagsReport {}
