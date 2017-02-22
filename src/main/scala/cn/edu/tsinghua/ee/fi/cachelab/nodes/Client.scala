package cn.edu.tsinghua.ee.fi.cachelab.nodes

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator

import cn.edu.tsinghua.ee.fi.cachelab.util.URL

object Client extends EndNodeCreator {
  def props(config: Config) = Props(new Client(config))
}

class Client(config: Config) extends AbstractNodeActor(config) {
  def sendHttpRequest(url: URL) {
    
  }
  
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
}