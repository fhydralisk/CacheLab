package cn.edu.tsinghua.ee.fi.cachelab.algorithm

import cn.edu.tsinghua.ee.fi.cachelab.cacheobject.CacheObject

trait CacheAlgorithm {
  def putObject(cacheObject: CacheObject): Boolean
  def getObject(name: String): Option[CacheObject]
  def contains(name: String): Boolean
}