package cn.edu.tsinghua.ee.fi.cachelab.nodes

import akka.actor.Actor
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodePath
import com.typesafe.config.Config


object MetricsMessages {
  object Cache {

    case object CacheHit

    case object CacheMiss

  }

}

trait MetricsContainer {
  def clear()
}

trait CacheMetricsContainer extends MetricsContainer{
  def hit(ts: Int)
  def miss(ts: Int)
}

class DefaultCacheMetricsContainer extends CacheMetricsContainer {

  protected var wrappedContainer: Map[Int, List[Int, Int]] = Map()
  override def hit(ts: Int) {
    putElement(ts, 0)
  }

  override def miss(ts: Int) {
    putElement(ts, 1)
  }

  override def clear() {
    wrappedContainer = Map()
  }

  protected def putElement(ts: Int, pos: Int): Unit = {
    val core = pos match {
      case 0 =>
        List(1, 0)
      case 1 =>
        List(0, 1)
    }

    wrappedContainer = wrappedContainer + (ts -> ((wrappedContainer getOrElse(ts, List(0, 0)), core).zipped map {
      (a, b) => a + b
    }))
  }
}

/**
  * Created by hydra on 2017/3/16.
  */
class Metrics(name: String, config: Config) extends AbstractEndPoint(name, config) {

  import MetricsMessages._

  val cacheMetrics: CacheMetricsContainer = new DefaultCacheMetricsContainer

  override def nodeMsg: Actor.Receive = {
    case Cache.CacheHit =>
      cacheMetrics.hit(tickCounter)
    case Cache.CacheMiss =>
      cacheMetrics.miss(tickCounter)
    case _ =>

  }

  override def tick() {

  }

  def receiveMessage[T, E](msg: T, path: EndNodePath[E]): Unit = {
    log.warning(s"Metrics actor should not receive message via ReceiveMessage method. msg: $msg")
  }
}
