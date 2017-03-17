package cn.edu.tsinghua.ee.fi.cachelab.algorithm

import java.util.Map.Entry

/**
  * Created by hydra on 2017/3/17.
  */
class LRUCache2[K, V](cacheSize: Int, delegate: K => Unit)
  extends java.util.LinkedHashMap[K, V](Math.ceil(cacheSize / 0.75).toInt + 1, 0.75f, true) {

  private val MAX_CACHE_SIZE = cacheSize

  override def removeEldestEntry(eldest: Entry[K, V]): Boolean = {
    if (size() > MAX_CACHE_SIZE) {
      delegate(eldest.getKey)
      true
    } else {
      false
    }
  }

}
