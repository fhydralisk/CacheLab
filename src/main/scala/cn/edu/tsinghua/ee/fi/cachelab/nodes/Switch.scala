package cn.edu.tsinghua.ee.fi.cachelab.nodes

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator


object Switch extends EndNodeCreator {
  def props(config: Config) = Props(new Switch(config))
}

class Switch(config: Config) extends NetWare(config) {
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
  
  }
  
}