package reports.spark.segment

import java.net.URLDecoder

import enums.RefererSegment
import infrastructure.{DateFormatUtils, DateUtils}
import infrastructure.transform.StringLineTransforming
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import reports.Report
import reports.ReportTypes._
import reports.spark.{PeriodHdfsReport, PageviewsBasedSparkReport}

case class SegmentThroughPeriodsReportItem(browserId: BrowserId, time: Time, date: IntDate, segment: RefererSegment, url: Url)

trait SegmentThroughPeriodsReport extends PageviewsBasedSparkReport with Report {
  type Report = Array[(Array[TimeInterval], (BrowserCount, PageViewsCount))]
  type InputType = SegmentThroughPeriodsReportItem

  val name = "SegmentThroughPeriodsReport"

  val analyticsPeriods : Array[TimeInterval]

  val browserInTargetSegment: Seq[InputType] => Boolean = (views: Seq[InputType]) => {

    val getSearchQuery: Url => String = (url: Url) => {
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
          case e: Exception => ""
        }
      }
    }

    val queryContainsNypost: String => Boolean = (query: String) =>
      query.contains("new york post") || query.contains("ny post") || query.contains("nypost")

    val x = (views, views.groupBy(y => y.date))
    (x._2.size >= 3
      || (
      x._1.size >= 3
        && (
        x._1.exists(y => y.segment == RefererSegment.Direct.id)
          ||
          x._1.exists(y => y.segment == RefererSegment.Search.id && queryContainsNypost(getSearchQuery(y.url)))
        )
      )
    )
  }

  def report(rdd : RDD[InputType]) : Report = {

    val localAnalyticsPeriods = analyticsPeriods.sortBy(x => x.begin)
    val localBrowserInTargetSegment = browserInTargetSegment

    val res: RDD[Map[TimeInterval, (Boolean, Int)]] =
      rdd.keyBy(x => x.browserId).groupByKey(groupByKeyPartitionCountBigData)
        .map(x => x._2)
        // add period to each view
        .map(x =>
          x.map(y => (y, localAnalyticsPeriods.find(period => y.time >= period.begin && y.time < period.end)))
            .filter(y => y._2.nonEmpty)
            .map(y => (y._1, y._2.get)))
        // group by period
        .map(x => x.groupBy(y => y._2))
        // count views within interval
        .map(x => x.mapValues(y => (localBrowserInTargetSegment(y.map(z => z._1)), y.size)))
        .filter(x => x.getOrElse(localAnalyticsPeriods(0), (false, 0))._1)

    val patterns =
      (for (i <- 1 to localAnalyticsPeriods.length) yield
        (for (j <- 0 to i - 1) yield localAnalyticsPeriods(j)).toArray
      ).toArray

    patterns.map(pattern =>
      (
        pattern,
        res
          // get browsers that appeared in all pattern intervals
          .filter(x => pattern.forall(y => x.getOrElse(y, (false, 0))._1))
          // filter browser data -> remove intervals that are not contained in pattern
          .map(x => x.filter(y => pattern.contains(y._1)))
          .toArray()
          // count pageviews within browser
          .map(x => x.toArray.map(y => y._2._2).reduce(_ + _))
      )
    )
    .map(x => (x._1, (x._2.size, x._2.reduce(_ + _))))
  }

  def output(report : Report) : Iterator[String] = {

    def printTimeInterval(x : TimeInterval) : String = {
      s"${DateFormatUtils.dateFormatYYYYMMDD(new DateTime(x.begin))}-${DateFormatUtils.dateFormatYYYYMMDD(new DateTime(DateUtils.plusDaysToTimestamp(x.end, -1)))}"
    }

    report
      .sortBy(x => x._1(0).begin)
      .map(x => s"time intervals: ${x._1.map(y => printTimeInterval(y)).mkString(", ")}, browsers: ${x._2._1}, pageviews: ${x._2._2}")
      .toIterator
  }

  override def prepareInputRDD(inputRDD: RDD[StringLineTransforming]): RDD[InputType] = {
    inputRDD.map(x => SegmentThroughPeriodsReportItem(x.browserId, x.time, x.date, x.refererSegmentId, x.rawReferer))
  }
}

case class SegmentThroughPeriodsHdfsReport(appId : String, beginDate : DateTime, endDate : DateTime,
                                          override val analyticsPeriods : Array[TimeInterval]
                                                   ) extends PeriodHdfsReport(appId, beginDate, endDate) with SegmentThroughPeriodsReport {
  override val specNameParam = s"${(analyticsPeriods).map(x => s"t${x.begin}-t${x.end}").mkString("_")}"
}
