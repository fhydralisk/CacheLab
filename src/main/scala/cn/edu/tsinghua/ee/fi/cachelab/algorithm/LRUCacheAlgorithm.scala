package cn.edu.tsinghua.ee.fi.cachelab.algorithm
import cn.edu.tsinghua.ee.fi.cachelab.cacheobject.CacheObject

/**
  * Created by hydra on 2017/3/13.
  */
class LRUCacheAlgorithm(volume: Int) extends CacheAlgorithm {

  protected val cache = new LRUCache2[String, CacheObject](volume, flushCacheObject)
  protected var cacheIndex: Map[String, Set[String]] = Map()

  override def putObject(cacheObject: CacheObject): Boolean = {
    if (cacheObject.getSize() > volume || cacheObject.getSize() <= 0)
      false
    else {

      val splitCache: Set[String] = ((1 to cacheObject.getSize()) map { n =>
        s"${cacheObject.getName()}$n"
      }).toSet

      cacheIndex = cacheIndex + (cacheObject.getName() -> splitCache)

      splitCache foreach {
        putBlock(_, cacheObject)
      }

      true
    }
  }

  override def getObject(name: String): Option[CacheObject] = {
    val cacheObject = (cacheIndex filterKeys { _ == name }).headOption.flatMap {
      nm => cache.get(s"${nm}1") match {
        case null =>
          // TODO: log debug message here. Cache name appears in cacheIndex but cache
          None
        case e @ _ =>
          // Refresh all blocks of it
          cacheIndex.get(name) foreach { blockSet =>
            blockSet foreach {
              putBlock(_, e)
            }
          }
          Some(e)
      }
    }

    cacheObject
  }

  override def contains(name: String): Boolean = cacheIndex.contains(name)

  private def putBlock(blockName: String, cacheObject: CacheObject): Unit = {
    cache.remove(blockName)
    cache.put(blockName, cacheObject)
  }

  private def flushCacheObject(evict: String): Unit = {
    // DANGEROUS: DO NOT REMOVE ELEMENT WITH KEY evict FOR LINKEDHASHMAP WILL AUTO REMOVE IT
    val cacheName = cache.get(evict).getName()

    cacheIndex.get(cacheName) foreach {
      _.filterNot {
        _ == evict
      } foreach cache.remove
    }

    cacheIndex = cacheIndex - cacheName
  }

}