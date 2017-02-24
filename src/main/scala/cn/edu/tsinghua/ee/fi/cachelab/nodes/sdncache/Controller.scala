package cn.edu.tsinghua.ee.fi.cachelab.nodes.sdncache

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodePath
import cn.edu.tsinghua.ee.fi.cachelab.nodes.AbstractEndPoint


object Controller extends EndNodeCreator {
  def props(name: String, config: Config) = Props(new Controller(name, config))
}

class Controller(name: String, config: Config) extends AbstractEndPoint(name, config) {
  
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
  
  def receiveMessage[T, E](message: T, path: EndNodePath[E]) {
    
  }
  
}