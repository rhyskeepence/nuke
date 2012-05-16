package sky.sns.nuke

import mongo.{ArchiveScript, MongoStorage}
import org.scala_tools.time.Imports._

class Nuker(mongo: MongoStorage) {
  def archiveAll(periodToKeep: Period) {
    mongo.collectionNames.foreach(archiveCollection(periodToKeep))
  }

  private def archiveCollection(periodToKeep: Period)(collectionName: String) {
    mongo.withCollection(collectionName) (ArchiveScript.archiveCollection(periodToKeep))
  }
}