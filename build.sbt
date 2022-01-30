ThisBuild / scalaVersion := "2.13.4"

Global / protodepUseHttps := true

inThisBuild(
  List(
    organization := "com.coralogix",
    homepage     := Some(url("https://github.com/coralogix/coralogix-jdbc")),
    licenses     := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "fokot",
        "Frantisek Kocun",
        "frantisek@coralogix.com",
        url("https://www.coralogix.com")
      )
    )
  )
)

val shade = "canttouchthis.@0"

lazy val root = (project in file("."))
  .settings(
    name := "coralogix-jdbc",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % "1.0.3",
      "dev.zio" %% "zio-test"     % "1.0.3" % Test,
      "dev.zio" %% "zio-test-sbt" % "1.0.3" % Test,
      // gRPC deps:
      "com.thesamet.scalapb"               %% "scalapb-runtime-grpc"                    % scalapb.compiler.Version.scalapbVersion,
      "io.grpc"                             % "grpc-netty"                              % "1.34.0",
      "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % "1.18.1-1" % "protobuf",
      "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % "1.18.1-1"
    ),
    excludeDependencies ++= Seq(ExclusionRule("io.circe")),
    scalacOptions --= Seq("-language:implicitConversions", "-Wunused:params"),
    scalacOptions ~= { _.filterNot(_.startsWith("-Wunused")) },
    scalacOptions += "-Wconf:any:warning-verbose",
    testFrameworks ++= Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    scalafmtOnCompile           := true,
    fork                        := true, // Always fork so we can override environment variables
    Test / fork                 := true, // Always fork so we can override environment variables
    run / envVars               := Map("CORALOGIX_CONFIG" -> "src/main/resources/dev.conf"),
    assemblyJarName in assembly := "coralogix-driver.jar",
    assemblyMergeStrategy in assembly := {
      case "META-INF/MANIFEST.MF" => MergeStrategy.discard
      case _                      => MergeStrategy.first
    },
    assemblyShadeRules in assembly := Seq(
      ShadeRule
        .rename(
          "com.coralogix.**" -> "@0",
          "io.grpc.**"       -> "@0",
          "android.**"       -> shade,
          "com.**"           -> shade,
          "google.**"        -> shade,
          "grpc.**"          -> shade,
          "io.**"            -> shade,
          "izumi.**"         -> shade,
          "org.**"           -> shade,
          "scala.**"         -> shade,
          "scalapb.**"       -> shade,
          "scalapb_circe.**" -> shade,
          "scalapb_json.**"  -> shade,
          "zio.**"           -> shade,
          "*.**"             -> "@0"
        )
        .inAll
    ),
    skip in publish := true
  )
  .dependsOn(grpcDeps)

lazy val grpcDeps = LocalProject("grpc-deps")
grpcDeps / Compile / scalacOptions --= Seq("-Wunused:imports", "-Xfatal-warnings")

// empty project just for the sake of publishing fat jat
lazy val driver = project
  .settings(
    name := "coralogix-jdbc",
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "-" + module.revision + "." + artifact.extension
    },
    packageBin in Compile := (assembly in (root, Compile)).value,
    crossVersion          := CrossVersion.disabled,
    libraryDependencies   := Nil
  )
