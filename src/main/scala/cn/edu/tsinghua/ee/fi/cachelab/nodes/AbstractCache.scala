package cn.edu.tsinghua.ee.fi.cachelab.nodes

import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeActorMessageProcess
import cn.edu.tsinghua.ee.fi.cachelab.algorithm.CacheAlgorithm
import cn.edu.tsinghua.ee.fi.cachelab.messages.Http.HttpRequest
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeActor.EnvelopProcess.Unpack
import cn.edu.tsinghua.ee.fi.cachelab.topo.Envelope.Envelope


abstract class AbstractCache(name: String, config: Config) extends MiddleBox(name, config) {
  def volume: Int
  val cacheAlgorithm: CacheAlgorithm

  override def receiveEnvelope[T, E](envl: Envelope[T, E]): EndNodeActorMessageProcess = {
    envl.message match {
      case HttpRequest(url) if cacheAlgorithm.contains(url) && useCacheFor(url) =>
        Unpack()
      case _ =>
        super.receiveEnvelope(envl)
    }
  }

  def useCacheFor(url: String): Boolean
}