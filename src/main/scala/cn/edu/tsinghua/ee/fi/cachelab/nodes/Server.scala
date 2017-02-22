package cn.edu.tsinghua.ee.fi.cachelab.nodes

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator


object Server extends EndNodeCreator {
  def props(config: Config) = Props(new Server(config))
  
}

class Server(config: Config) extends AbstractNodeActor(config) {
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
}