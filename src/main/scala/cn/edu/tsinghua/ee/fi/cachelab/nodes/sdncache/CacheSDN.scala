package cn.edu.tsinghua.ee.fi.cachelab.nodes.sdncache

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodePath
import cn.edu.tsinghua.ee.fi.cachelab.nodes.AbstractCache


object CacheSDN extends EndNodeCreator {
  def props(name: String, config: Config) =  Props(new CacheSDN(name, config))
}

class CacheSDN(name: String, config: Config) extends AbstractCache(name, config) {
  
  val cacheAlgorithm = null
  def volume = 10
  
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
  
  def receiveMessage[T, E](message: T, path: EndNodePath[E]) {

  }
  
}