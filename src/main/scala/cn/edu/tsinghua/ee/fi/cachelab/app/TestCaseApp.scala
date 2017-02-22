package cn.edu.tsinghua.ee.fi.cachelab.app

import akka.actor.ActorSystem
import akka.pattern.ask
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.util.Timeout
import cn.edu.tsinghua.ee.fi.cachelab.topo.{EndNodeInfo, EndNodeCreator}
import cn.edu.tsinghua.ee.fi.cachelab.topo.CommonMapBasedTypeRegister
import cn.edu.tsinghua.ee.fi.cachelab.nodes.{Client, Server, Switch}
import cn.edu.tsinghua.ee.fi.cachelab.nodes.sdncache.{Controller, Redirector, CacheSDN}
import cn.edu.tsinghua.ee.fi.cachelab.deployment.MutableDeployer
import cn.edu.tsinghua.ee.fi.cachelab.topo.TopologyMessages._

object TestCaseApp {
  def main(args: Array[String]) {
    val nodeTypeMap: Map[String, EndNodeCreator] = Map(
        "Client" -> Client,
        "Server" -> Server,
        "Switch" -> Switch,
        "Controller" -> Controller,
        "Redirector" -> Redirector,
        "Cache" -> CacheSDN
    )
    val typeRegister = new CommonMapBasedTypeRegister(nodeTypeMap)
    implicit val system = ActorSystem.create("TestCaseSystem")
    val nodes: Iterable[EndNodeInfo] = 
      formTestNodes("Client", "c", 4) ++ 
      formTestNodes("Redirector", "r", 2) ++ 
      formTestNodes("Switch", "s", 3) ++ 
      formTestNodes("Cache", "cc", 2) ++ 
      formTestNodes("Server", "sv", 1) ++
      formTestNodes("Controller", "ct", 1) 
    
    val connection: Iterable[Tuple2[String, String]] = Set[Tuple2[String, String]](
        "c1" -> "r1", "c2" -> "r1", "c3" -> "r2", "c4" -> "r2",
        "r1" -> "s1", "r2" -> "s2", "s1" -> "s3", "s2" -> "s3",
        "s1" -> "cc1", "s2" -> "cc2",
        "s3" -> "sv1"
    )
    
    val deployer = new MutableDeployer(typeRegister, nodes, connection)
    val topoMan = system.actorSelection(s"/user/${deployer.topologyManagerName}")
    
    import system.dispatcher
    implicit val timeout: Timeout = 1 second
    
    println (s"Asking for path $topoMan")
    
    topoMan ? GetPath("c1", "sv1") map {
      case GetPathReply(s) =>
        println(s.getNodeSequence)
    } recover {
      case e: Throwable =>
        println(e)
    }
  }
  
  def formTestNodes(nodeType: String, nodeNamePrefix: String, count: Int) = {
    1 to count map { n =>
      val config = ConfigFactory.parseString(s"name = $nodeNamePrefix$n")
      new EndNodeInfo(nodeType, s"$nodeNamePrefix$n", config)
    } toSet
  }
}