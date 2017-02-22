package cn.edu.tsinghua.ee.fi.cachelab.nodes.sdncache

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.nodes.AbstractNodeActor


object Controller extends EndNodeCreator {
  def props(name: String, config: Config) = Props(new Controller(name, config))
}

class Controller(name: String, config: Config) extends AbstractNodeActor(name, config) {
  
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
}