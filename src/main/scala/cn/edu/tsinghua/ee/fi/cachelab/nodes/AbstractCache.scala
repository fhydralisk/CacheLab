package cn.edu.tsinghua.ee.fi.cachelab.nodes

import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.{EndNodeActor, EndNodeCreator}

import cn.edu.tsinghua.ee.fi.cachelab.algorithm.CacheAlgorithm


abstract class AbstractCache(name: String, config: Config) extends MiddleBox(name, config) {
  def volume: Int
  val cacheAlgorithm: CacheAlgorithm
  
}