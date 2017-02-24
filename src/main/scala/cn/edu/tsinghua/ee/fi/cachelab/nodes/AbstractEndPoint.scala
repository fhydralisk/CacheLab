package cn.edu.tsinghua.ee.fi.cachelab.nodes

import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.Envelope

abstract class AbstractEndPoint(name: String, config: Config) extends AbstractNodeActor(name, config) {
  def forwardEnvelope[T, E](envl: Envelope.Envelope[T, E]) = {
    log.debug("Endpoint shall not receive forwarded envelope")
  }
}