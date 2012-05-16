package sky.sns.nuke

import mongo.MongoStorage
import org.scala_tools.time.Imports._

object NukeApp extends App {

  val usage = "Usage: nuke [mongo-host] [mongo-port] [mongo-user] [mongo-pass] [days-to-keep]"

  args.toList match {
    case mongoHost :: mongoPort :: mongoUser:: mongoPass :: daysToKeep :: tail =>
      val periodToKeep = daysToKeep.toInt.days
      val storage = new MongoStorage(mongoHost, mongoPort.toInt, mongoUser, mongoPass)
      new Nuker(storage).archiveAll(periodToKeep)

    case _ =>
      println(usage)
  }
}

