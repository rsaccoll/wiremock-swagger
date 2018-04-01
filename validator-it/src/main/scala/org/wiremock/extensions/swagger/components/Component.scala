package org.wiremock.extensions.swagger.components

import com.typesafe.config.Config
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.wiremock.extensions.swagger.environment._

abstract class Component(configPath:String){

  lazy val config   :Config = environmentConfig.getConfig(configPath)
  lazy val name     :String = config.getString("name")
  lazy val baseUrl  :String = config.getString("baseUrl")

  def given(): RequestSpecification = {
    RestAssured.given(
      new RequestSpecBuilder().setBaseUri(baseUrl)
        .build()
    )
  }

  def setup(): Unit = {}

  def teardown(): Unit = {}

}
