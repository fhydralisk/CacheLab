package cn.edu.tsinghua.ee.fi.cachelab.nodes.sdncache

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.nodes.MiddleBox

object Redirector extends EndNodeCreator {
  def props(config: Config) = Props(new Redirector(config))  
}

class Redirector(config: Config) extends MiddleBox(config) {
  val redirectTable: RedirectTable = new RedirectTable
  
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
  
}

class RedirectTable {
  
}