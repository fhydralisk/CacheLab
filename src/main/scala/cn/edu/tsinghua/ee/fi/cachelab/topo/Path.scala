package cn.edu.tsinghua.ee.fi.cachelab.topo

import collection.JavaConverters._
import org.jgrapht.GraphPath

trait Path[V, E] {
  def getNodeSequence: Iterable[V]
}


object JGraphTBasedPathImpl {
  def apply[V, E](path: GraphPath[V, E]) = {
    new JGraphTBasedPathImpl[V, E](path)
  }
}


class JGraphTBasedPathImpl[V, E](path: GraphPath[V, E]) extends Path[V, E]{
  def getNodeSequence: Iterable[V] = {
    path.getVertexList().asScala
  }
}