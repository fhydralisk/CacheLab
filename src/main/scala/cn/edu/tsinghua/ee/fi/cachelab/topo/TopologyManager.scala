package cn.edu.tsinghua.ee.fi.cachelab.topo

import akka.actor.{Actor, ActorLogging, Props}


object TopologyManager {
  def props(topology: MutableTopology[Unit], mapper: EndNodeMapper): Props = Props(new TopologyManager(topology, mapper))
}

class TopologyManager(topology: MutableTopology[Unit], mapper: EndNodeMapper) extends Actor with ActorLogging {
  import TopologyMessages._
  def receive = {
    case GetPath(from, to) =>
      log.debug("Receiving getpath message")
      try {
        val path = topology.getShortestPath(mapper.contextFromName(from).get, mapper.contextFromName(to).get)
        sender ! GetPathReply(path)
      } catch {
        case ex : java.util.NoSuchElementException =>
          sender ! akka.actor.Status.Failure(ex)
      }

    case _ =>
  }
}