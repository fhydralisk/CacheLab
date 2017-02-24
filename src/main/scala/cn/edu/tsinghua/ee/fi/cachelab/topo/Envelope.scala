package cn.edu.tsinghua.ee.fi.cachelab.topo

object Envelope {
  case class Envelope[T, E](message: T, path: EndNodePath[E], isAsk: Boolean, askTimeoutMs: Long)
}