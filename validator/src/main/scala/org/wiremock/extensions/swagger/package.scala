package org.wiremock.extensions

import java.net.URL
import java.nio.file.Files.{exists, isReadable}
import java.nio.file.Path

import io.swagger.util.Yaml.mapper

import scala.io.Source.fromInputStream

package object swagger {
  def processSwaggerDefinition(swaggerPath:Path): Option[String] = {
    Some(swaggerPath)
        .filter(path => exists(path) && isReadable(path))
        .map   (path => processSwaggerDefinition(path.toUri.toURL))
  }

  def processSwaggerDefinition(swaggerUrl:URL): String = {
    val payload = fromInputStream(swaggerUrl.openStream())
      .getLines()
      .mkString("\n")

    if (payload.startsWith("{")) payload else mapper().readTree(payload).toString
  }
}
