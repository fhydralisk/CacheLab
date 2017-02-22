package cn.edu.tsinghua.ee.fi.cachelab.nodes.sdncache

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.nodes.AbstractNodeActor


object Controller extends EndNodeCreator {
  def props(config: Config) = Props(new Controller(config))
}

class Controller(config: Config) extends AbstractNodeActor(config) {
  
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
}