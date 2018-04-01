package org.wiremock.extensions.swagger

import java.util.Collections.{emptyList, emptyMap}
import java.util.Date

import com.github.tomakehurst.wiremock.http.HttpHeader.httpHeader
import com.github.tomakehurst.wiremock.http.RequestMethod.GET
import com.github.tomakehurst.wiremock.http._
import com.github.tomakehurst.wiremock.verification.LoggedRequest

import scala.io.Source.fromInputStream

trait TestContext {

  val swaggerJsonPayload:String = fromPayload("/swagger.json")
  val swaggerYamlPayload:String = fromPayload("/swagger.yaml")

  val healthRequest          :Request  = buildRequest ("/v1/health?fresh=true")
  val invalidRequest         :Request  = buildRequest ("/v2/health")
  val healthResponse         :Response = buildResponse(200, """{"status":"OK"}""")
  val invalidResponse        :Response = buildResponse(200, """{"status":"UNKNOWN"}""")
  val responseInvalidRequest :Response = buildResponse(500, """{"messages":[{"key":"validation.request.path.missing","level":"ERROR","message":"No API path found that matches request '/v2/health'.","additionalInfo":[]}]}""")
  val responseInvalidResponse:Response = buildResponse(500, """{"messages":[{"key":"validation.schema.enum","level":"ERROR","message":"[Path '/status'] Instance value (\"UNKNOWN\") not found in enum (possible values: [\"OK\",\"NOK\"])","additionalInfo":[]}]}""")

  def fromPayload(payload:String):String = {
    fromInputStream(getClass.getResourceAsStream(payload))
      .getLines()
      .mkString("\n")
  }

  def buildRequest(path:String): Request = {
    val headers = new HttpHeaders( new HttpHeader("Accept", "application/json"))
    new LoggedRequest(path, s"http2://localhost:8080$path", GET, "127.0.0.1", headers, emptyMap(), false, new Date(), null, null, emptyList())
  }

  def buildResponse(statusCode:Int, body:String): Response = {
    val headers = new HttpHeaders(httpHeader("Content-Type", "application/json"))
    new Response.Builder()
      .status (statusCode)
      .body   (body)
      .headers(headers)
      .build()
  }
}
