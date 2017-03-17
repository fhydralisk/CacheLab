package cn.edu.tsinghua.ee.fi.cachelab.messages

object Http {
  case class HttpRequest(url: String)
  case class HttpResponse(len: Int)
  
}