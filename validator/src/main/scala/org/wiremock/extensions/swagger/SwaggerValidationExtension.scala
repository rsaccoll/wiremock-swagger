package org.wiremock.extensions.swagger

import java.net.URI
import java.nio.file.Paths.get

import com.atlassian.oai.validator.SwaggerRequestResponseValidator.createFor
import com.atlassian.oai.validator.model
import com.atlassian.oai.validator.model.{SimpleRequest, SimpleResponse}
import com.atlassian.oai.validator.report.ValidationReport
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.common.Urls.splitQuery
import com.github.tomakehurst.wiremock.extension.{Parameters, ResponseTransformer}
import com.github.tomakehurst.wiremock.http._
import io.swagger.util.Json
import org.wiremock.extensions.swagger.SwaggerValidationExtension.{defaultSwaggerDefinition, defaultSwaggerExtName, validate}

import scala.collection.convert.ImplicitConversionsToScala._
import scala.language.implicitConversions


/**
  * Swagger Validation Extension
  */
class SwaggerValidationExtension() extends ResponseTransformer{
  lazy val definitionOpt: Option[String] = processSwaggerDefinition(get(defaultSwaggerDefinition))

  override def transform(request: Request, response: Response, files: FileSource, parameters: Parameters): Response = {
    validate(definitionOpt)(request, response)
  }

  override def getName: String = defaultSwaggerExtName
}

object SwaggerValidationExtension{

  val defaultSwaggerExtName    = "swagger-validation-extension"
  val defaultSwaggerDefinition = "./swagger-payload"

  def validate(definitionOpt:Option[String])(request:Request, response:Response):Response = {
    definitionOpt
      .map       ( validateUsingDefinition( request, response ) )
      .filter    ( _.hasErrors )
      .map       ( buildResponseWithErrors )
      .getOrElse ( response )
  }

  private def buildResponseWithErrors(result:ValidationReport): Response ={
    Response.response()
      .status(500)
      .headers(new HttpHeaders(new HttpHeader("Content-Type", "application/json")))
      .body   (Json.mapper().writeValueAsBytes(result))
      .build()
  }

  private def validateUsingDefinition(request:Request, response: Response) = {
    (swaggerJsonUrlOrPayload:String) => {
      createFor(swaggerJsonUrlOrPayload).build().validate( request, response )
    }
  }

  implicit private def toRequest(request: Request): model.Request = {
    val uri     = URI.create(request.getUrl)
    val builder = new SimpleRequest.Builder(request.getMethod.getName, uri.getPath)
      .withBody(request.getBodyAsString)

    request.getHeaders.all.foreach((header: HttpHeader) => {
      builder.withHeader(header.key, header.values)
    })

    splitQuery(uri).foreach({
      case (key:String, value: QueryParameter) => builder.withQueryParam(key, value.values())
    })
    builder.build
  }

  implicit def toResponse(response: Response): model.Response = {
    val builder = new SimpleResponse.Builder(response.getStatus)
      .withBody(response.getBodyAsString)

    response.getHeaders.all.foreach( header =>
      builder.withHeader(header.key, header.values)
    )
    builder.build
  }
}
