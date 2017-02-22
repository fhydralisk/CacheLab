package cn.edu.tsinghua.ee.fi.cachelab.nodes

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator


object Switch extends EndNodeCreator {
  def props(name: String, config: Config) = Props(new Switch(name, config))
}

class Switch(name: String, config: Config) extends NetWare(name, config) {
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
  
  }
  
}