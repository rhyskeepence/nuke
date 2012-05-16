package sky.sns.nuke.mongo

import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import scala.collection.JavaConverters._
import com.mongodb.casbah.map_reduce.MapReduceInlineOutput
import org.scala_tools.time.Imports._
import com.mongodb.casbah.query.Imports._
import DailyAggregation._

object ArchiveScript {

  def archiveCollection(period: Period)(collection: MongoCollection) {
    println("Archiving " + collection.name)

    val cutoffTime = DateTime.now.minusWeeks(12)

    val metricNames = collection.find()
      .sort(MongoDBObject("_id" -> -1))
      .limit(1)
      .toList
      .headOption
      .map(mongoObj => mongoObj.keySet().asScala.toList)
      .getOrElse(List[String]())
      .filter(_ != "_id")

    val aggregatedArchive = collection.mapReduce(
      mapFunctionFor(metricNames),
      reduceFunctionFor(metricNames),
      MapReduceInlineOutput,
      Some("_id" $lt cutoffTime.millis),
      None,
      None,
      Some(finalizeFunctionFor(metricNames)))

    collection.remove("_id" $lt cutoffTime.millis)

    aggregatedArchive
      .map(toArchiveObject(metricNames))
      .foreach(collection += _)
  }

  private def toArchiveObject(metricNames: List[String]) = { mapReduceOutput: DBObject =>
    val timestamp = mapReduceOutput.getOrElse("_id", 0)

    val archiveObjectBuilder = MongoDBObject.newBuilder
    archiveObjectBuilder += "_id" -> timestamp

    for {
      results <- mapReduceOutput.getAs[DBObject]("value")
      metric <- metricNames
      averagedValue <- results.getAs[Double](metric + "_avg")
    } (archiveObjectBuilder += metric -> averagedValue)

    archiveObjectBuilder.result()
  }

}