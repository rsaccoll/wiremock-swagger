package org.wiremock.extensions.swagger

import io.restassured.module.scala.RestAssuredSupport._
import org.hamcrest.Matchers.equalTo
import org.scalatest.FeatureSpec

class SwaggerValidationSpecs
  extends FeatureSpec
  with Context
{

  info("As a User,")
  info("I should be able to use Wiremock-Swagger-Validation")
  info("To validate all requests/responses stubbed by wiremock")

  feature("Should succeed"){
    scenario("and get a response when both requests and responses are valid"){
      mockComponent.withPath("/v1/health", Map("response" -> "valid"))
        .Then()
        .statusCode(200)
        .body("status", equalTo("OK"))
    }
  }

  feature("Should fail"){
    scenario("when the request is not valid"){
      mockComponent.withPath("/v2/health")
        .Then()
        .statusCode(500)
        .body("messages[0].key", equalTo("validation.request.path.missing"))
        .body("messages[0].level", equalTo("ERROR"))
        .body("messages[0].message", equalTo("No API path found that matches request '/v2/health'."))
    }

    scenario("when the response is not valid"){
      mockComponent.withPath("/v1/health", Map("response" -> "invalid"))
        .Then()
        .statusCode(500)
        .body("messages[0].key", equalTo("validation.schema.enum"))
        .body("messages[0].level", equalTo("ERROR"))
        .body("messages[0].message", equalTo("[Path '/status'] Instance value (\"ERROR\") not found in enum (possible values: [\"OK\",\"NOK\"])"))
    }
  }
}
