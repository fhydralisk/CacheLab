package cn.edu.tsinghua.ee.fi.cachelab.deployment

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import cn.edu.tsinghua.ee.fi.cachelab.topo.{EndNodeInfo}


object DeployMessages {
  case class DeployNode(info: EndNodeInfo, connect: Iterable[String])
  case class DeployNodeAck()
  case class DestoryNode(name: String)
}

object DeployManager {
  def props(deployer: MutableDeployment[Unit]) = Props(new DeployManager(deployer))
}


class DeployManager(deployer: MutableDeployment[Unit]) extends Actor with ActorLogging {
  import DeployMessages._
  import context.dispatcher
  def receive = {
    case DeployNode(info, connect) =>
      try {
        val result = deployer.deploy(info, connect)
        result map { _ => DeployNodeAck() } pipeTo sender
      } catch {
        case ex: Throwable =>
          sender ! akka.actor.Status.Failure(ex)
      }
    case DestoryNode(name) =>
      try {
        deployer.destroy(name)
      } catch {
        case ex: Throwable =>
          sender ! akka.actor.Status.Failure(ex)
      }
  }
}