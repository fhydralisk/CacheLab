package cn.edu.tsinghua.ee.fi.cachelab.nodes

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodePath


object Switch extends EndNodeCreator {
  def props(name: String, config: Config) = Props(new Switch(name, config))
}

class Switch(name: String, config: Config) extends NetWare(name, config) {
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
  
  }
  
  def receiveMessage[T, E](message: T, path: EndNodePath[E]) {
    
  }
  
}