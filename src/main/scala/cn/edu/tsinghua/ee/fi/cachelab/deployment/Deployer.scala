package cn.edu.tsinghua.ee.fi.cachelab.deployment

import com.typesafe.config.Config
import akka.actor.{ActorSystem, Actor, ActorRef, PoisonPill}
import akka.pattern.ask
import akka.util.Timeout
import concurrent.duration._
import collection.JavaConverters._
import util.{Success, Failure}
import concurrent.{Future, Await}
import cn.edu.tsinghua.ee.fi.cachelab.topo._


object Deployer {
  type NodeConnectInfo = Tuple2[String, String]  
}


private[deployment] trait Deployment[E] {
  type NodeMapType <: collection.Map[String, EndNodeContext]
  type TopologyType <: ImmutableTopology[E]
  
  val nodeMap: NodeMapType
  val topology: TopologyType
}

private[deployment] trait ImmutableDeployment[E] extends Deployment[E] {
  type NodeMapType = collection.Map[String, EndNodeContext]
  type TopologyType = ImmutableTopology[E]
}


private[deployment] trait MutableDeployment[E] extends Deployment[E] {
  override type NodeMapType = collection.concurrent.Map[String, EndNodeContext]
  type TopologyType = MutableTopology[E]
  
  def deploy(endNode: EndNodeInfo, connect: Iterable[String]): Future[EndNodeContext]
  def destroy(name: String)
}


private[deployment] object EmptyEndNodeContext extends EndNodeContext(null, null, null)


abstract class AbstractDeployer(
    typeRegister: NodeTypeRegister, 
    nodeInfos: Iterable[EndNodeInfo], 
    linkedInfos: Iterable[Deployer.NodeConnectInfo]
    )(implicit system: ActorSystem) extends Deployment[Unit] with EndNodeMapper {
  
  protected val nodeFactory: EndNodeFactory = new RegisteredEndNodeFactory(typeRegister)
  protected val nodeSupervisor: ActorRef
  val nodeSupervisorName = "NodeSupervisor"
  
  protected def doDeployNode(info: EndNodeInfo) = {
    import NodeSupervisorMessages._
    import system.dispatcher
    implicit val timeout: Timeout = 500 millis
    
    nodeSupervisor ? DeployNode(info) map {
      case DeployNodeReply(ctx) =>
        ctx
    }
  }
  
  protected def deployEndNodes(endNodeInfos: Iterable[EndNodeInfo]): collection.Map[String, Future[EndNodeContext]] = {
    var nodeMapLocal = Map[String, Future[EndNodeContext]]()
    endNodeInfos.foreach { info =>  
      if (nodeMapLocal contains info.endNodeName) {
        throw new java.lang.IllegalArgumentException("Duplicated node names.")
      }
      
      nodeMapLocal += info.endNodeName -> doDeployNode(info)
    }
    nodeMapLocal
  }
  
  protected def translateTopology(linkedInfos: Iterable[Deployer.NodeConnectInfo], nodeMap: collection.Map[String, EndNodeContext]) = {
    val topoLinkMap = linkedInfos map { 
      case (n1, n2) if (nodeMap contains n1) && (nodeMap contains n2) => nodeMap.get(n1).get -> nodeMap.get(n2).get
      case _ => throw new java.lang.IllegalArgumentException("Node not found in deployed list");
      }
    
    topoLinkMap
  }

  def contextFromName(endNodeName: String): Option[EndNodeContext] = nodeMap get endNodeName filter { _ != EmptyEndNodeContext }
}


class ImmutableDeployer(
    typeRegister: NodeTypeRegister, 
    nodeInfos: Iterable[EndNodeInfo], 
    linkedInfos: Iterable[Deployer.NodeConnectInfo]
    )(implicit system: ActorSystem) 
    extends AbstractDeployer(typeRegister, nodeInfos, linkedInfos) with ImmutableDeployment[Unit] {
  
  protected val nodeSupervisor = system.actorOf(NodeSupervisor.props(nodeFactory), nodeSupervisorName)
  
  val nodeMap: NodeMapType = createNodeMap(nodeInfos)
  val topology: TopologyType = createTopology(linkedInfos)

  def this(typeRegister: NodeTypeRegister)(implicit system: ActorSystem) = this(typeRegister, Iterable.empty, Iterable.empty)(system)

  protected def createTopology(linkedInfos: Iterable[Deployer.NodeConnectInfo]): TopologyType = {
    new ImmutableTopologyUnwightedImpl(nodeMap.values, translateTopology(linkedInfos, nodeMap))
  }
  
  protected def createNodeMap(nodeInfos: Iterable[EndNodeInfo]): NodeMapType = {
    import system.dispatcher
    val name2future = deployEndNodes(nodeInfos)
    val futureMap = name2future map {
      case (name, future) =>
        future map {
          name -> _
        }
    }
    
    val seq = Future.sequence(futureMap)
    Await.result(seq, 2 second).toMap
  }
}


class MutableDeployer(
    typeRegister: NodeTypeRegister, 
    nodeInfos: Iterable[EndNodeInfo], 
    linkedInfos: Iterable[Deployer.NodeConnectInfo]
    )(implicit system: ActorSystem) 
    extends AbstractDeployer(typeRegister, nodeInfos, linkedInfos) with MutableDeployment[Unit] {
  
  protected val nodeSupervisor = system.actorOf(NodeSupervisor.props(nodeFactory), nodeSupervisorName)

  val nodeMap: NodeMapType = createNodeMap(nodeInfos)
  val topology: TopologyType = createTopology(linkedInfos)
  
  val topologyManagerName = "TopologyManager"
  val deployManagerName = "DeployManager"
  
  protected val topologyManager = system.actorOf(TopologyManager.props(topology, this), topologyManagerName)
  protected val deployManager = system.actorOf(DeployManager.props(this), deployManagerName) 
  
  

  import system.dispatcher
  
  protected def setFutureNodeMap(nodeName: String, nodeContextFuture: Future[EndNodeContext]) = {
    nodeMap += nodeName -> EmptyEndNodeContext
    
    nodeContextFuture map { e =>
      nodeMap get nodeName filter (_ == EmptyEndNodeContext) map { _ => nodeMap += nodeName -> e; Unit } getOrElse {
        // Maybe some called destroy before it has been created successfully
        system.actorSelection(e.endNodePath) ! PoisonPill
        //throw new java.lang.RuntimeException("EndNodeContext placeholder (EmptyEndNodeContext) lost.")
      }
      e
    } recover { 
      case e: Throwable =>
        nodeMap get nodeName filter (_ == EmptyEndNodeContext) map { _ => nodeMap -= nodeName }
        throw e
    }
  }
  
  protected def createTopology(linkedInfos: Iterable[Deployer.NodeConnectInfo]): TopologyType = {
    new MutableTopologyUnwightedImpl(nodeMap.values, translateTopology(linkedInfos, nodeMap))
  }
  
  protected def createNodeMap(nodeInfos: Iterable[EndNodeInfo]): NodeMapType = {
    val name2future = deployEndNodes(nodeInfos)
    val futureMap = name2future map {
      case (name, future) =>
        future map {
          name -> _
        }
    }
    
    val seq = Future.sequence(futureMap)
    new java.util.concurrent.ConcurrentHashMap[String, EndNodeContext]().asScala ++= Await.result(seq, 2 second).toMap
  }
  
  def deploy(endNode: EndNodeInfo, connect: Iterable[String]) = {
    val deployResult = doDeployNode(endNode)
    setFutureNodeMap(endNode.endNodeName, deployResult) map { ctx => 
      topology.addEndNode(ctx)
      connect foreach { contextFromName(_) foreach { t => topology.addEdge(ctx -> t)} }
      ctx
    }
  }
  
  def destroy(name: String) {
    val nodeContext = contextFromName(name)
    nodeContext foreach { n =>
      system.actorSelection(n.endNodePath) ! PoisonPill
      topology -= n
      nodeMap -= n.endNodeName
    }
  }
}
