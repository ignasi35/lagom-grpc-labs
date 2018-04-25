import com.lightbend.sbt.javaagent.JavaAgent.JavaAgentKeys.javaAgents
import akka.grpc.gen.scaladsl.ScalaClientCodeGenerator
import play.sbt.PlayAkkaHttp2Support


organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `lagom-grpc-labs` = (project in file("."))
  .aggregate(`hello-api`, `hello-impl`)

lazy val `hello-api` = (project in file("hello-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `hello-impl` = (project in file("hello-impl"))
  .enablePlugins(LagomScala
    && PlayAkkaHttp2Support
    && AkkaGrpcPlugin
  )
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    libraryDependencies ++= Seq(
      macwire,
      scalaTest
    ),
  )
  .settings(
    PB.protoSources in Compile += sourceDirectory.value / "protobuf",
    (akkaGrpcCodeGenerators in Compile) := Seq(
      GeneratorAndSettings(ScalaClientCodeGenerator, (akkaGrpcCodeGeneratorSettings in Compile).value))
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`hello-api`)



lagomKafkaEnabled in ThisBuild := false
lagomCassandraEnabled in ThisBuild := false

lagomUnmanagedServices in ThisBuild += ("echo.Echo" -> "https://127.0.0.1:8443")
