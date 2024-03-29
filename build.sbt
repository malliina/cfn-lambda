inThisBuild(
  Seq(
    scalaVersion := "3.2.2",
    version := "0.0.1",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
)

val lambda = project
  .in(file("lambda"))
  .settings(
    libraryDependencies ++= Seq("classic", "core").map { m =>
      "ch.qos.logback" % s"logback-$m" % "1.4.5"
    } ++ Seq(
      "org.slf4j" % "slf4j-api" % "2.0.6",
      "com.amazonaws" % "aws-java-sdk-s3" % "1.12.409",
      "com.amazonaws" % "aws-lambda-java-core" % "1.2.2",
      "com.amazonaws" % "aws-lambda-java-events" % "3.11.0"
    ),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) =>
        xs.map(_.toLowerCase) match {
          case "services" :: xs => MergeStrategy.filterDistinctLines
          case _                => MergeStrategy.discard
        }
      case _ => MergeStrategy.first
    },
    assembly := {
      val src = assembly.value
      val dest = baseDirectory.value / "function.jar"
      IO.copyFile(src, dest)
      streams.value.log.info(s"Copied '$src' to '$dest'.")
      dest
    }
  )

val cdkVersion = "2.65.0"
val cdk = project
  .in(file("cdk"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      "cdkVersion" -> cdkVersion
    ),
    libraryDependencies ++= Seq(
      "software.amazon.awscdk" % "aws-cdk-lib" % cdkVersion
    )
  )

val root = project.in(file(".")).aggregate(lambda, cdk)
