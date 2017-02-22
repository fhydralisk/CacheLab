package cn.edu.tsinghua.ee.fi.cachelab.topo

import akka.actor.{Actor, ActorRef, Props, ActorSystem, ActorContext}
import com.typesafe.config.Config
import cn.edu.tsinghua.ee.fi.cachelab.messages.Tick


trait EndNodeActor extends Actor {
  def tick(): Unit
  override def receive: Actor.Receive = tickMsg orElse nodeMsg
  def tickMsg: Actor.Receive = {
    case _ @ Tick => this.tick()
  }
  def nodeMsg: Actor.Receive
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
  def props(config: Config): Props
}


trait EndNodeMapper {
  def contextFromName(name: String): Option[EndNodeContext]
}


case class EndNodeContext(endNodeRef: ActorRef, endNodeName: String, protected val endNodeConfig: Config)


class CommonMapBasedTypeRegister(nodeTypeRegister: Map[String, EndNodeCreator]) extends NodeTypeRegister {
  def typeOf(nodeType: String) = nodeTypeRegister.get(nodeType)
}


class RegisteredEndNodeFactory(nodeTypeRegister: NodeTypeRegister) extends EndNodeFactory {
  
  def createEndNodeInSystem(nodeType: String, name: String, config: Config)(implicit system: ActorSystem) = {
    nodeTypeRegister.typeOf(nodeType).map { 
      c => EndNodeContext(system.actorOf(c.props(config)), name, config) 
    }
  }
  
  def createEndNodeInActor(nodeType: String, name: String, config: Config)(implicit context: ActorContext) = {
    nodeTypeRegister.typeOf(nodeType).map { 
      c => EndNodeContext(context.actorOf(c.props(config)), name, config) 
    }
  }
}
