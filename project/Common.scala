import sbt.Keys._
import sbt.{AutoPlugin, _}
import scoverage.ScoverageKeys._

object Common extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    moduleName        := "com.github.wiremock.extensions.swagger",
    version           := "0.1.0-SNAPSHOT",
    scalaVersion      := "2.12.4",
    scalacOptions     ++= Seq(
      "-target:jvm-1.8",
      "-feature",
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Xfuture"
    ),
    publishMavenStyle := true,
    pomIncludeRepository := { x ⇒ false },
    javacOptions ++= Seq(
      "-Xlint:unchecked"
    ),
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    parallelExecution      in Test   := false,
    coverageFailOnMinimum            := false,
    coverageHighlighting             := true,
    autoAPIMappings                  := true
  )
}
