package cn.edu.tsinghua.ee.fi.cachelab.nodes

import com.typesafe.config.Config
import akka.pattern.ask
import akka.actor.ActorRef
import akka.util.Timeout
import akka.actor.ActorLogging
import akka.pattern.AskTimeoutException
import concurrent.duration._
import util.Try
import cn.edu.tsinghua.ee.fi.cachelab.topo.{EndNodeActor, EndNodeCreator, Envelope, EndNodePath}

abstract class AbstractNodeActor(name: String, config: Config) extends EndNodeActor with ActorLogging {
  val topoManName = Try(config.getString("topology.manager.name")).getOrElse("TopologyManager")
  val deployManName = Try(config.getString("deployer.manager.name")).getOrElse("DeployManager")
  
  import context.dispatcher
  
  def sendTo(dest: String, message: Any) {
    getPathTo(dest) map { path =>
      val envl = pack(message, path, false, 0)
      path.getNextNodeFromString(name) map { ectx =>
        context.actorSelection(ectx.endNodePath) ! envl
      } getOrElse {
        // None stands for the fact that this actor is the destination.
        // Someone send message to self
        // TODO: Deal with this message
        throw new java.lang.IllegalArgumentException("Dest should not be self")
      }
    } recover {
      case ex: Throwable =>
        log.warning(s"SendTo failed when getting path to $dest, got exception $ex")
        throw ex
    }
  }
  
  def askTo(dest: String, message: Any)(implicit timeout: Timeout) = {
    getPathTo(dest) flatMap { path =>
      val envl = pack(message, path, true, timeout.duration.toMillis)
      path.getNextNodeFromString(name) map { ectx =>
        context.actorSelection(ectx.endNodePath) ? envl map {
          case e : Envelope.Envelope[_, _] =>
            e.message
          case e @ _ =>
            log.warning(s"Get message that has not been packed into envelope $e")
            e
        }
      } getOrElse {
        // None stands for the fact that this actor is the destination.
        // Someone send message to self
        // TODO: Deal with this message
        throw new java.lang.IllegalArgumentException("Dest should not be self")
      }
    } recover {
      case ex: Throwable =>
        log.warning(s"AskTo failed when getting path to $dest, got exception $ex")
        throw ex
    }
  }
  
  def replyTo(asker: ActorRef, message: Any) {
    asker ! pack(message, null, false, 0)
  }
  
  def receiveEnvelope[T, E](envl: Envelope.Envelope[T, E]) = {
    import EndNodeActor.EnvelopProcess._
    try {
      envl.path.getNextNodeFromString(name) map {_ => 
        // Receive a envelop with the destination different from self, do not handle this message,
        // Subclasses such as router should override this method to handle this envelope
        Unhandled()
      } getOrElse {
        // 
        Unpack()
      }
    } catch {
      case ex : java.lang.NoSuchFieldException =>
        log.warning("Cannot find node in path")
        Unhandled()
    }
  }
  
  def unhandledEnvelope[T, E](envl: Envelope.Envelope[T, E]) {
    log.debug(s"unhandeld Envelop, message: ${envl.message}, path: ${envl.path}")
  }
  
  def getPathTo(dest: String) = {
    import cn.edu.tsinghua.ee.fi.cachelab.topo.TopologyMessages._
    val topoMan = actorSelectionOfName(topoManName)
    implicit val askTimeout: Timeout = 500 millis
    
    topoMan ? GetPath(from=name, to=dest) map {
      case GetPathReply(path) => 
        path
      case _ =>
        throw new java.lang.NoSuchFieldException(s"Cannot find a path to $dest")
    }
  }
  
  def pack(message: Any, path: EndNodePath[_], isAsk: Boolean, timeout: Long) = Envelope.Envelope(message, path, isAsk, timeout)
  
  def actorSelectionOfName(name: String) = context.actorSelection(s"/user/$name")
}
