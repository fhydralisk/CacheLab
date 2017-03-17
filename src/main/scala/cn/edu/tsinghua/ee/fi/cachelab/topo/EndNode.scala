package cn.edu.tsinghua.ee.fi.cachelab.topo

import akka.actor.{Actor, ActorRef, ActorPath, Props, ActorSystem, ActorContext}
import com.typesafe.config.{Config, ConfigValueFactory}
import cn.edu.tsinghua.ee.fi.cachelab.messages.Tick


trait EndNodeActorMessageProcess


object EndNodeActor {
  object EnvelopProcess {
    case class Handled() extends EndNodeActorMessageProcess
    case class Unhandled() extends EndNodeActorMessageProcess
    case class Unpack() extends EndNodeActorMessageProcess
    case class Forward() extends EndNodeActorMessageProcess
  }
}

trait EndNodeActor extends Actor {
  def tick(): Unit
  override def receive: Actor.Receive = tickMsg orElse envelopeMsg orElse nodeMsg
  
  def tickMsg: Actor.Receive = {
    case _ : Tick =>
      this.tick()
      tickCounter += 1
  }
  
  def envelopeMsg: Actor.Receive = {
    case envl : Envelope.Envelope[_, _] =>
      receiveEnvelope(envl) match {
        case EndNodeActor.EnvelopProcess.Unhandled() =>
          unhandledEnvelope(envl)
        case EndNodeActor.EnvelopProcess.Unpack() =>
          receiveMessage(envl.message, envl.path)
        case EndNodeActor.EnvelopProcess.Forward() =>
          forwardEnvelope(envl)
        case EndNodeActor.EnvelopProcess.Handled() =>
          // Subclasses has already handled this message, do nothing here
      }
  }
  
  // self defined messages is received by this par
  def nodeMsg: Actor.Receive
  
  def receiveEnvelope[T, E](envl: Envelope.Envelope[T, E]): EndNodeActorMessageProcess
  def unhandledEnvelope[T, E](envl: Envelope.Envelope[T, E])
  
  // Messages that shall be forward to other nodes is received by this methods
  def forwardEnvelope[T, E](envl: Envelope.Envelope[T, E])
  
  // Messages sent by SendTo/AskTo is received by this method
  def receiveMessage[T, E](msg: T, path: EndNodePath[E])
  
  var tickCounter = 0
}


case class EndNodeInfo(endNodeType: String, endNodeName: String, endNodeConfig: Config)


trait NodeTypeRegister {
  def typeOf(nodeType: String): Option[EndNodeCreator]
}


trait EndNodeFactory {
  def createEndNodeInSystem(nodeType: String, name: String, config: Config)(implicit system: ActorSystem): Option[EndNodeContext]
  def createEndNodeInActor(nodeType: String, name: String, config: Config)(implicit context: ActorContext): Option[EndNodeContext]
}


trait EndNodeCreator {
  def props(name: String, config: Config): Props
}


trait EndNodeMapper {
  def contextFromName(name: String): Option[EndNodeContext]
}


case class EndNodeContext(endNodePath: ActorPath, endNodeName: String, protected val endNodeConfig: Config)


class CommonMapBasedTypeRegister(nodeTypeRegister: Map[String, EndNodeCreator]) extends NodeTypeRegister {
  def typeOf(nodeType: String) = nodeTypeRegister.get(nodeType)
}


class RegisteredEndNodeFactory(nodeTypeRegister: NodeTypeRegister) extends EndNodeFactory {
  
  def createEndNodeInSystem(nodeType: String, name: String, config: Config)(implicit system: ActorSystem) = {
    nodeTypeRegister.typeOf(nodeType).map { 
      c => EndNodeContext(system.actorOf(c.props(name, config), name).path, name, defaultConfig(config, name)) 
    }
  }
  
  def createEndNodeInActor(nodeType: String, name: String, config: Config)(implicit context: ActorContext) = {
    nodeTypeRegister.typeOf(nodeType).map { 
      c => EndNodeContext(context.actorOf(c.props(name, config), name).path, name, defaultConfig(config, name)) 
    }
  }
  
  def defaultConfig(config: Config, name: String): Config = {
    config.withValue("name", ConfigValueFactory.fromAnyRef(name))
  }
}
