package org.wiremock.extensions.swagger

import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

class SwaggerValidationSpecs
  extends FreeSpec
  with Matchers
  with TestContext
  with OneInstancePerTest
{

  "Given the 'SwaggerValidation'" - {
    "when not using a swagger definition" - {
      // Check if is yaml or json
      val validatorExtension:SwaggerValidationExtension = new SwaggerValidationExtension(){
        override lazy val definitionOpt: Option[String] = None
      }
      validatorExtension.transform(healthRequest,  healthResponse,  null, null) shouldBe healthResponse
      validatorExtension.transform(healthRequest,  invalidResponse, null, null) shouldBe invalidResponse
      validatorExtension.transform(invalidRequest, healthResponse,  null, null) shouldBe healthResponse
    }

    "when using a Swagger definition" - {
      // Check if is yaml or json
      val validatorExtension:SwaggerValidationExtension = new SwaggerValidationExtension(){
        override lazy val definitionOpt: Option[String] = Some(swaggerJsonPayload)
      }

      "should have a proper name" in {
        validatorExtension.getName shouldBe "swagger-validation-extension"
      }

      "should fail if" - {
        "no request matches swagger" in {
          val result = validatorExtension.transform(invalidRequest, healthResponse, null, null)
          result.getStatus       shouldBe responseInvalidRequest.getStatus
          result.getHeaders      shouldBe responseInvalidRequest.getHeaders
          result.getBodyAsString shouldBe responseInvalidRequest.getBodyAsString
        }

        "no response matches swagger" in {
          val result = validatorExtension.transform(healthRequest, invalidResponse, null, null)
          result.getStatus       shouldBe responseInvalidResponse.getStatus
          result.getHeaders      shouldBe responseInvalidResponse.getHeaders
          result.getBodyAsString shouldBe responseInvalidResponse.getBodyAsString
        }
      }

      "should succeed if both request/response matches swagger" in {
        val result = validatorExtension.transform(healthRequest, healthResponse, null, null)
        result shouldBe healthResponse
      }
    }
  }
}
