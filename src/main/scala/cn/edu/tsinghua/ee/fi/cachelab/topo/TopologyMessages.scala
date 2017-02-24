package cn.edu.tsinghua.ee.fi.cachelab.topo

object TopologyMessages {
  case class GetPath(from: String, to: String)
  case class GetPathReply(path: EndNodePath[_])
}