package cn.edu.tsinghua.ee.fi.cachelab.topo

import collection.JavaConverters._
import org.jgrapht.GraphPath

trait Path[V, EW] {
  def getNodeSequence: Iterable[V]
  
  def getNextNode(node: V): Option[V] = {
    val it = getNodeSequence.iterator
    if (it.find( _ == node ) == None)
      throw new java.lang.IllegalArgumentException("endnode not in path")
    else if (it.hasNext) 
      Some(it.next()) 
    else
      None
  }
  
  def getNextNodeFromString(node: String): Option[V]
}

trait EndNodePath[EW] extends Path[EndNodeContext, EW] {
  def getNextNodeFromString(node: String) = {
    val it = getNodeSequence.iterator 
    if (it.find( _.endNodeName == node ) == None)
      throw new java.lang.IllegalArgumentException("endnode not in path")
    else if (it.hasNext) 
      Some(it.next()) 
    else 
      None
  }
}


abstract class JGraphTBasedPathImpl[V, EW](path: GraphPath[V, EW]) extends Path[V, EW]{
  def getNodeSequence: Iterable[V] = {
    path.getVertexList().asScala
  }
}