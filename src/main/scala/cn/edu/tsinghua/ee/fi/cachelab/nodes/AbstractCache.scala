package cn.edu.tsinghua.ee.fi.cachelab.nodes

import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.{EndNodeActor, EndNodeCreator}

import cn.edu.tsinghua.ee.fi.cachelab.algorithm.CacheAlgorithm

object AbstractCache extends EndNodeCreator {
  def props(config: Config) = {
    throw new java.lang.RuntimeException("Abstract actor cannot be created.")
  }
}

abstract class AbstractCache(config: Config) extends MiddleBox(config) {
  def volume: Int
  val cacheAlgorithm: CacheAlgorithm
  
}