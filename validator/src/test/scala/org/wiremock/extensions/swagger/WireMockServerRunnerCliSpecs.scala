package org.wiremock.extensions.swagger

import java.io.File

import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

import scala.io.Source

class WireMockServerRunnerCliSpecs
  extends FreeSpec
  with Matchers
  with TestContext
  with BeforeAndAfterAll
{
  val wiremockServerCli: WireMockServerRunnerCli = new WireMockServerRunnerCli {}
  val arguments        : Seq[String]             = Seq("--port", "8080", "--verbose", "--extensions=com.classA,com.classB")

  "Given the 'WireMockServerRunnerCli'" - {
    "when '--swagger-url' not is defined" - {
      "wiremock arguments should be kept" in {
        val result    = wiremockServerCli.processCliSwaggerOption(arguments)
        result shouldBe arguments
      }
    }

    "when '--swagger-url' is defined" - {
      "the 'swagger-payload' should exist" in {
        val swaggerUrl = getClass.getResource("/swagger.json").toURI.toURL
        val result     = wiremockServerCli.processCliSwaggerOption(Seq(s"--swagger-url=$swaggerUrl") ++ arguments)

        result shouldBe Seq("--port", "8080", "--verbose", "--extensions=com.classA,com.classB,org.wiremock.extensions.swagger.SwaggerValidationExtension")

        val payload = Source.fromFile(SwaggerValidationExtension.defaultSwaggerDefinition, "UTF-8").getLines().mkString("\n")
        payload shouldBe swaggerJsonPayload
      }
    }
  }

  override protected def beforeAll(): Unit =
    deleteDefinition()

  override protected def afterAll(): Unit =
    deleteDefinition()

  private def deleteDefinition():Unit = {
    val fileDefinition = new File(SwaggerValidationExtension.defaultSwaggerDefinition)
    if( fileDefinition.exists() ){
      fileDefinition.delete()
    }
  }
}
