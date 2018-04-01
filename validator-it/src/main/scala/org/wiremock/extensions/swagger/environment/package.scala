package org.wiremock.extensions.swagger

import com.typesafe.config.{Config, ConfigFactory}

package object environment {
  lazy val environmentConfig: Config =  ConfigFactory.load(s"application.conf")
}
