package cn.edu.tsinghua.ee.fi.cachelab.topo


import org.jgrapht.graph.{SimpleGraph, DefaultEdge}
import org.jgrapht.{Graph, GraphPath}
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import scala.collection.JavaConverters._

/**
 * Stores the topo of all end nodes.
 * 
 */
trait ImmutableTopology[EW] {  
  // get the shortest path form node to node
  val pathType: Class[_ <: EndNodePath[EW]]
  def getShortestPath(from: EndNodeContext, to: EndNodeContext): Path[EndNodeContext, EW]
}


trait MutableTopology[E] extends ImmutableTopology[E] {
  // Add Node Into Graph
  def addEndNode(endNode: EndNodeContext): MutableTopology[E]
  def +=(endNode: EndNodeContext) = addEndNode(endNode)
  
  // Remove Node from graph
  def removeEndNode(endNode: EndNodeContext): MutableTopology[E]
  def -=(endNode: EndNodeContext) = removeEndNode(endNode)
  
  // Add Edge between nodes
  def addEdge(edge: Tuple2[EndNodeContext, EndNodeContext]): MutableTopology[E]
  def +=(edge: Tuple2[EndNodeContext, EndNodeContext]) = addEdge(edge)
  
  // Remove Edge between nodes
  def removeEdge(edge: Tuple2[EndNodeContext, EndNodeContext]): MutableTopology[E]
  def -=(edge: Tuple2[EndNodeContext, EndNodeContext]) = removeEdge(edge)
}

trait TopologyWithDefaultPath[EW] extends ImmutableTopology[EW] {
  lazy val pathType = classOf[EndNodePathImpl[EW]]
}


trait TopologyWithGraph[V, E <: DefaultEdge] {
  protected val edgeType: Class[E]
  protected val topoGraph: Graph[V, E]
}

trait EndNodeTopologyWithGraph[E <: DefaultEdge] extends TopologyWithGraph[EndNodeContext, E] 

trait EndNodeTopologyWithSimpleGraph extends TopologyWithGraph[EndNodeContext, DefaultEdge] {
  lazy protected val topoGraph = new SimpleGraph[EndNodeContext, DefaultEdge](edgeType)
}


class EndNodePathImpl[E](path: GraphPath[EndNodeContext, E]) extends JGraphTBasedPathImpl[EndNodeContext, E](path) with EndNodePath[E]

abstract class ImmutableTopologyImpl[EW, ET <: DefaultEdge](
    nodes: Iterable[EndNodeContext],
    edges: Iterable[Tuple2[EndNodeContext, EndNodeContext]],
    protected val edgeType: Class[ET]
    ) extends ImmutableTopology[EW] with EndNodeTopologyWithGraph[ET] {
  
  nodes.foreach { topoGraph.addVertex }
  edges.foreach { e => topoGraph.addEdge(e._1, e._2) }
  
  // TEST: if topoGraph is modified, shall we create a new dijkstra alg? Result: It's OKAY
  val dijkstraAlg = new DijkstraShortestPath[EndNodeContext, ET](topoGraph)
  
  def getShortestPath(from: EndNodeContext, to: EndNodeContext): Path[EndNodeContext, EW] = {
    if (topoGraph.containsVertex(from) && topoGraph.containsVertex(to)) {
      val constructor = pathType.getConstructor(classOf[GraphPath[EndNodeContext, _ <: DefaultEdge]])
      constructor.newInstance(dijkstraAlg.getPath(from, to)) 
    } else {
      throw new java.lang.IllegalArgumentException("No such node")
    }
  }
}

abstract class MutableTopologyImpl[EW, ET <: DefaultEdge](
    nodes: Iterable[EndNodeContext],
    edges: Iterable[Tuple2[EndNodeContext, EndNodeContext]],
    edgeType: Class[ET]
    ) extends ImmutableTopologyImpl[EW, ET](nodes, edges, edgeType) with MutableTopology[EW] {
  
  def this(edgeType: Class[ET]) = this(Iterable.empty, Iterable.empty, edgeType)
    
  def addEndNode(endNode: EndNodeContext) = {
    this.topoGraph.addVertex(endNode)
    this
  }
  
  def removeEndNode(endNode: EndNodeContext) = {
    this.topoGraph.removeVertex(endNode)
    this
  }
  
  def addEdge(edge: Tuple2[EndNodeContext, EndNodeContext]) = {
    this.topoGraph.addEdge(edge._1, edge._2)
    this
  }
  
  def removeEdge(edge: Tuple2[EndNodeContext, EndNodeContext]) = {
    this.topoGraph.removeEdge(edge._1, edge._2)
    this
  }
  
}

class ImmutableTopologyUnwightedImpl(
    nodes: Iterable[EndNodeContext],
    edges: Iterable[Tuple2[EndNodeContext, EndNodeContext]]
    ) 
    extends ImmutableTopologyImpl[Unit, DefaultEdge](nodes, edges, classOf[DefaultEdge]) 
    with EndNodeTopologyWithSimpleGraph
    with TopologyWithDefaultPath[Unit]
    

class MutableTopologyUnwightedImpl(
    nodes: Iterable[EndNodeContext],
    edges: Iterable[Tuple2[EndNodeContext, EndNodeContext]]
    ) 
    extends MutableTopologyImpl[Unit, DefaultEdge](nodes, edges, classOf[DefaultEdge]) 
    with EndNodeTopologyWithSimpleGraph
    with TopologyWithDefaultPath[Unit]