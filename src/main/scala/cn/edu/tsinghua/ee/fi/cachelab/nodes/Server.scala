package cn.edu.tsinghua.ee.fi.cachelab.nodes

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodePath

import cn.edu.tsinghua.ee.fi.cachelab.messages.TestCase._


object Server extends EndNodeCreator {
  def props(name: String, config: Config) = Props(new Server(name, config))
  
}

class Server(name: String, config: Config) extends AbstractEndPoint(name, config) {
  def nodeMsg = {
    case _ =>
  }
  
  def tick() {
    
  }
  
  def receiveMessage[T, E](message: T, path: EndNodePath[E]) {
    message match {
      case TestEmptyMessage(ask) =>
        log.info("TestMessage received")
        if (ask) {
          log.info(s"Replying to $sender")
          replyTo(sender, "Hello, here")
        }
    }
  }
  
}