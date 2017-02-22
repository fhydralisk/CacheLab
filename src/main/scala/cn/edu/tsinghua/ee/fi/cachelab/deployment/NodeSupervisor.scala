package cn.edu.tsinghua.ee.fi.cachelab.deployment

import akka.actor.{Actor, ActorLogging, Props, OneForOneStrategy}
import akka.actor.SupervisorStrategy._
import cn.edu.tsinghua.ee.fi.cachelab.topo.{EndNodeFactory, EndNodeInfo, EndNodeContext}
import scala.concurrent.duration._


object NodeSupervisorMessages {
  case class DeployNode(info: EndNodeInfo)
  case class DeployNodeReply(context: EndNodeContext)
}


object NodeSupervisor {
  def props(nodeFactory: EndNodeFactory) = Props(new NodeSupervisor(nodeFactory))
}


class NodeSupervisor(nodeFactory: EndNodeFactory) extends Actor with ActorLogging {
  import NodeSupervisorMessages._
  def receive = {
    case DeployNode(info) =>
      try {
        val ctx = nodeFactory.createEndNodeInActor(info.endNodeType, info.endNodeName, info.endNodeConfig).get
        sender ! DeployNodeReply(ctx)
      } catch {
        case ex: Throwable =>
          sender ! akka.actor.Status.Failure(ex)
      }
    case _ =>
  }
  
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case t =>
      super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
  }
  
}