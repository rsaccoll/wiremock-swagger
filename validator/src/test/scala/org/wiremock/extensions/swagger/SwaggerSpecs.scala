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
      val jsonPayload = processSwaggerDefinition(toPath("/swagger.json"))
      toJsonNode(jsonPayload) shouldBe toJsonNode(Some(swaggerJsonPayload))
    }

    "should be possible to load from a YamlPayload" in {
      val jsonPayload = processSwaggerDefinition(toPath("/swagger.yaml"))
      toJsonNode(jsonPayload) shouldBe toJsonNode(Some(swaggerJsonPayload))
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
