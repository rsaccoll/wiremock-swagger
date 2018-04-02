package org.wiremock.extensions.swagger

import java.nio.file.{Path, Paths}

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.scalatest.{FreeSpec, Matchers}

import scala.language.implicitConversions

class SwaggerSpecs
  extends FreeSpec
  with Matchers
  with TestContext
{
  val mapper      = new ObjectMapper()

  "Given 'processSwaggerDefinition'" - {
    "should be possible to load from a JsonPayload" in {
      println(s"URI From '/swagger.json' - ${getClass.getResource("/swagger.json").toURI}")
      val jsonPayload = processSwaggerDefinition(toPath("/swagger.json"))
      println(jsonPayload)
//      toJsonNode(jsonPayload) shouldBe toJsonNode(Some(swaggerJsonPayload))
    }

    "should be possible to load from a YamlPayload" in {
      println(s"URI From '/swagger.yaml' - ${getClass.getResource("/swagger.yaml").toURI}")
      val jsonPayload = processSwaggerDefinition(toPath("/swagger.yaml"))
      println(jsonPayload)
//      toJsonNode(jsonPayload) shouldBe toJsonNode(Some(swaggerJsonPayload))
    }

    "should be None if the payload does not exist" in {
      processSwaggerDefinition(Paths.get("/swagger-invalid.yaml")) shouldBe None
    }
  }

  private def toJsonNode(payload:Option[String]):Option[JsonNode] = {
    payload.map(mapper.reader().readTree)
  }

  implicit private def toPath(fileName:String):Path = {
    Paths.get(getClass.getResource(fileName).toURI)
  }
}
