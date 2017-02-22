package cn.edu.tsinghua.ee.fi.cachelab.nodes.sdncache

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.nodes.AbstractCache


object CacheSDN extends EndNodeCreator {
  def props(config: Config) =  Props(new CacheSDN(config))
}

class CacheSDN(config: Config) extends AbstractCache(config) {
  
  val cacheAlgorithm = null
  def volume = 10
  
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
  
}