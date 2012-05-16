package sky.sns.nuke.mongo

import com.mongodb.casbah.{MongoCollection, MongoConnection}

class MongoStorage(host: String, port: Int, username: String, password: String) {
  private val mongo = MongoConnection(host, port)

  def withCollection[T](collectionName: String)(actionOnCollection: MongoCollection => T) = {
    val snoggin = mongo("snoggin")

    val collection = snoggin(collectionName)
    actionOnCollection(collection)
  }

  def collectionNames = {
    val mongo = MongoConnection(host, port)
    mongo("snoggin").getCollectionNames()
  }

  def close() {
    mongo.close()
  }
}
