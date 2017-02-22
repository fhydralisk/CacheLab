package cn.edu.tsinghua.ee.fi.cachelab.nodes

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator


object Server extends EndNodeCreator {
  def props(name: String, config: Config) = Props(new Server(name, config))
  
}

class Server(name: String, config: Config) extends AbstractNodeActor(name, config) {
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
}