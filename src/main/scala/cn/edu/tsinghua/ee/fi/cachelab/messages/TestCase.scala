package cn.edu.tsinghua.ee.fi.cachelab.messages

object TestCase {
  case class TestSendMessage(to: String, message: Any)
  case class TestAskMessage(to: String, message: Any)
  case class TestEmptyMessage(ask: Boolean)
}