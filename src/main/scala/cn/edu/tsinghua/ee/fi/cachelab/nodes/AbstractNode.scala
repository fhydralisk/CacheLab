package cn.edu.tsinghua.ee.fi.cachelab.nodes

import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.{EndNodeActor, EndNodeCreator}

abstract class AbstractNodeActor(name: String, config: Config) extends EndNodeActor {
  
}
