import Dependencies._
import sbt._

import scala.util.Try


lazy val wiremockSwagger = (project in file("."))
  .aggregate(validator)
  .settings(Common.projectSettings)
  .settings(
    publishArtifact:= false
  )

lazy val validator = (project in file("validator"))
  .settings(
    name := "wiremock-swagger-validator",
    test                  in assembly     := {},
    target                in assembly     := baseDirectory.value / "target",
    mainClass             in assembly     := Some("org.wiremock.extensions.swagger.WireMockServerRunner.scala"),
    assemblyJarName       in assembly     := s"${name.value}.jar",
    assemblyMergeStrategy in assembly     := {
      x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    validatorDependencies
  )

lazy val `validator-it` = (project in file("validator-it"))
  .settings(
    name := "wiremock-swagger-validator-it",
    envVars := {
      def queryDockerInstancePort(serviceName:String, port:Int, default:String = ""):String = {
        Try{
          val ipAndPort = s"docker-compose -f ./validator-it/docker-compose.yml port $serviceName $port".!!
          ipAndPort.trim.split(':').last
        }.getOrElse(default)
      }

      Map(
        "WIREMOCK_PORT" -> queryDockerInstancePort("wiremock-swagger-validator", 8080)
      )
    },
    assembleArtifact := false,
    publishArtifact  := false,
    validatorItDependencies
  )
