package cn.edu.tsinghua.ee.fi.cachelab.nodes

import com.typesafe.config.Config


abstract class NetWare(name: String, config: Config) extends MiddleBox(name, config)