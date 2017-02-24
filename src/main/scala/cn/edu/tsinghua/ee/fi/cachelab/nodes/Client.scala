package cn.edu.tsinghua.ee.fi.cachelab.nodes

import akka.actor.Props
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeCreator
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodePath

import cn.edu.tsinghua.ee.fi.cachelab.util.URL

object Client extends EndNodeCreator {
  def props(name: String, config: Config) = Props(new Client(name, config))
}

class Client(name: String, config: Config) extends AbstractEndPoint(name, config) {
  import cn.edu.tsinghua.ee.fi.cachelab.messages.TestCase._
  import context.dispatcher
  
  def sendHttpRequest(url: URL) {
    
  }
  
  def receiveMessage[T, E](message: T, path: EndNodePath[E]) {
    
  }
  
  def nodeMsg = {
    case TestSendMessage(to, msg) =>
      log.info(s"Sending test message to $to, msg $msg")
      sendTo(to, msg)
    case TestAskMessage(to, msg) =>
      import concurrent.duration._
      import akka.util.Timeout
      log.info(s"Asking test message to $to, msg $msg")
      implicit val timeout: Timeout = 1 second
      
      askTo(to, msg) map { msg => log.info(s"Received reply of $msg") } recover { case ex @ _ => log info ex.toString() }
    case _ =>
  }
  
  def tick() {
    
  }
}