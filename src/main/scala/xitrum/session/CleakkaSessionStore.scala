package xitrum.session

class CleakkaSessionStore extends ServerSessionStore {
  private[this] val store = new cleakka.Cache(10)  // FIXME

  def get(sessionId: String): Option[Map[String, Any]] =
    store.get[Map[String, Any]](sessionId)

  def put(sessionId: String, immutableMap: Map[String, Any]) {
    store.put(sessionId, immutableMap)
  }

  def remove(sessionId: String) {
    store.remove(sessionId)
  }
}
