package cn.edu.tsinghua.ee.fi.cachelab.app

import akka.actor.ActorSystem
import cn.edu.tsinghua.ee.fi.cachelab.topo.{EndNodeInfo, EndNodeCreator}
import cn.edu.tsinghua.ee.fi.cachelab.topo.CommonMapBasedTypeRegister
import cn.edu.tsinghua.ee.fi.cachelab.nodes._
import cn.edu.tsinghua.ee.fi.cachelab.nodes.sdncache._
import cn.edu.tsinghua.ee.fi.cachelab.deployment.MutableDeployer

object TestCaseApp {
  def main(args: Array[String]) {
    val nodeTypeMap: Map[String, EndNodeCreator] = Map(
        "Client" -> Client,
        "Server" -> Server,
        "Controller" -> Controller,
        "Redirector" -> Redirector,
        "Cache" -> CacheSDN
    )
    val typeRegister = new CommonMapBasedTypeRegister(nodeTypeMap)
    implicit val system = ActorSystem.create("TestCaseSystem")
    val nodes: Iterable[EndNodeInfo] = List()
    val connection: Iterable[Tuple2[String, String]] = Set()
    val deployer = new MutableDeployer(typeRegister, nodes, connection)
  }
}