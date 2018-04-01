package org.wiremock.extensions.swagger.components

import io.restassured.response.Response

import scala.collection.convert.ImplicitConversionsToJava._

case class MockComponent() extends Component("wiremock"){

  def withPath(path:String, queryParams:Map[String, String] = Map.empty): Response = {
    given()
      .queryParams(queryParams)
      .get(path)
  }
}
