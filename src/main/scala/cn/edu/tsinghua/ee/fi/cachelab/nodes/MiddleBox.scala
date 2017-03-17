package cn.edu.tsinghua.ee.fi.cachelab.nodes

import com.typesafe.config.Config
import akka.pattern.{ask, pipe}
import cn.edu.tsinghua.ee.fi.cachelab.topo.Envelope
import cn.edu.tsinghua.ee.fi.cachelab.topo.EndNodeActor
import concurrent.duration._
import akka.util.Timeout


abstract class MiddleBox(name: String, config: Config) extends AbstractNodeActor(name, config) {
  
  override def receiveEnvelope[T, E](envl: Envelope.Envelope[T, E]) = {
    import  EndNodeActor.EnvelopProcess._
    super.receiveEnvelope(envl) match {
      case Unhandled() =>
        try {
          envl.path.getNextNodeFromString(name) map { _ =>
            Forward()
          } getOrElse {
            log.debug("Unhandled envelope which should be marked as Unpack.")
            Unpack()
          }
        } catch {
          case ex: Throwable =>
            log.warning(s"Error when proceeding envelope when getting next node, got exception $ex")
            Unhandled()
        }
      case e @ _ =>
        e
    }
  }
  
  def forwardEnvelope[T, E](envl: Envelope.Envelope[T, E]) {
    envl.path.getNextNodeFromString(name) foreach { ectx =>
      
      if (envl.isAsk) {
        import context.dispatcher
        log.debug(s"trying to forward message to ${ectx.endNodeName}, ${ectx.endNodePath}")
        implicit val timeout: Timeout = envl.askTimeoutMs millis
        
        context.actorSelection(ectx.endNodePath) ? envl pipeTo sender
      } else {
        context.actorSelection(ectx.endNodePath) ! envl
      }
    }
  }
  
}