package cn.edu.tsinghua.ee.fi.cachelab.nodes.lrucache

import akka.actor.Props
import cn.edu.tsinghua.ee.fi.cachelab.algorithm.LRUCacheAlgorithm
import cn.edu.tsinghua.ee.fi.cachelab.messages.Http.HttpRequest
import cn.edu.tsinghua.ee.fi.cachelab.nodes.AbstractCache
import cn.edu.tsinghua.ee.fi.cachelab.topo.{EndNodeCreator, EndNodePath}
import com.typesafe.config.Config

object CacheLRU extends EndNodeCreator {
  override def props(name: String, config: Config) = Props(new CacheLRU(name, config))
}

class CacheLRU(name: String, config: Config) extends AbstractCache(name, config) {
  val cacheAlgorithm = new LRUCacheAlgorithm(config.getInt("Volume"))
  def volume = 10

  def nodeMsg = {
    case _ =>
  }

  def tick() {

  }

  def receiveMessage[T, E](message: T, path: EndNodePath[E]) {
    message match {
      case HttpRequest(url) =>
    }
  }

  override def useCacheFor(url: String): Boolean = {
    true
  }
}