import sbt.Keys._
import sbt._

object Dependencies {
  lazy val scalaTest            = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val wiremock             = "com.github.tomakehurst" % "wiremock" % "2.16.0"
  lazy val swaggerValidatorCore = "com.atlassian.oai" % "swagger-request-validator-core" % "1.3.9"
  lazy val guava                = "com.google.guava" % "guava" % "20.0"
  lazy val findBugs             = "com.google.code.findbugs" % "jsr305" % "3.0.2"
  lazy val junit                = "junit" % "junit" % "4.12"
  lazy val restAssured          = "io.rest-assured" % "scala-support" % "3.0.7"
  lazy val config               = "com.typesafe" % "config" % "1.3.3"

  lazy val validatorDependencies = Seq(
    libraryDependencies ++= Seq(
      wiremock,
      swaggerValidatorCore,
      scalaTest % Test
    ),
    dependencyOverrides ++= Set(
      guava,
      findBugs,
      junit % Provided
    )
  )

  lazy val validatorItDependencies = Seq(
    libraryDependencies ++= Seq(
      config,
      restAssured,
      scalaTest % Test
    )
  )
}
