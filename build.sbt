val scalaFunction = project
  .in(file("."))
  .settings(
    version := "0.0.1",
    scalaVersion := "2.13.3",
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk-s3" % "1.11.819",
      "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
      "com.amazonaws" % "aws-lambda-java-events" % "3.1.0",
      "org.scalameta" %% "munit" % "0.7.9" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", _ @_*) => MergeStrategy.discard
      case _                           => MergeStrategy.first
    },
    assembly := {
      val src = assembly.value
      val dest = baseDirectory.value / "function.jar"
      IO.copyFile(src, dest)
      streams.value.log.info(s"Copied '$src' to '$dest'.")
      dest
    }
  )
