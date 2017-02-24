package cn.edu.tsinghua.ee.fi.cachelab.nodes.sdncache

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodePath
import cn.edu.tsinghua.ee.fi.cachelab.nodes.MiddleBox

object Redirector extends EndNodeCreator {
  def props(name: String, config: Config) = Props(new Redirector(name, config))  
}

class Redirector(name: String, config: Config) extends MiddleBox(name, config) {
  val redirectTable: RedirectTable = new RedirectTable
  
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
  
  def receiveMessage[T, E](message: T, path: EndNodePath[E]) {
    
  }
}

class RedirectTable {
  
}