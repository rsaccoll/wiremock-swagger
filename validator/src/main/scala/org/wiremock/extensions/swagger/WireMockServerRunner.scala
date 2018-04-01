package org.wiremock.extensions.swagger

import java.io.PrintWriter
import java.net.URL

import com.github.tomakehurst.wiremock.standalone

import scala.io.Source.fromInputStream

trait WireMockServerRunnerCli {
  private val swaggerCommandPrefix     : String            = "--swagger-url="
  private val isSwaggerDefinition      : String => Boolean = _.startsWith(swaggerCommandPrefix)
  private val extensionCommandPrefix   : String            = "--extensions="
  private val isExtensionDefinition    : String => Boolean = _.startsWith(extensionCommandPrefix)
  private val extensionCommandSeparator: String            = ","

  private def processSwaggerDefinition(swaggerUrl:String): Unit = {
    val payload = fromInputStream(new URL(swaggerUrl).openStream())
      .getLines()
      .mkString("\n")

    new PrintWriter(SwaggerValidationExtension.defaultSwaggerDefinition) {
      write(payload)
      close()
    }
  }

  def processCliSwaggerOption(args:Seq[String]): Seq[String] ={
    args.find( isSwaggerDefinition ) match {
      case Some(swaggerUrlCmd) =>
        processSwaggerDefinition(swaggerUrlCmd.replace(swaggerCommandPrefix, ""))

        val oldExtensions:Seq[String] = args.find(isExtensionDefinition)
          .map(_.replace(extensionCommandPrefix, "").split(extensionCommandSeparator).toSeq)
          .getOrElse(Seq.empty[String])

        val extensions = oldExtensions :+ classOf[SwaggerValidationExtension].getName
        val argsCleanedUp = args.filterNot( arg => isSwaggerDefinition(arg) || isExtensionDefinition(arg) )

        argsCleanedUp :+ s"$extensionCommandPrefix${extensions.mkString(extensionCommandSeparator)}"
      case None => args
    }
  }
}

object WireMockServerRunner extends App with WireMockServerRunnerCli{
  new standalone.WireMockServerRunner().run( processCliSwaggerOption(args):_*)
}
