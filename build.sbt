val scalaFunction = project
  .in(file("."))
  .settings(
    version := "0.0.1",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-core" % "1.2.0",
      "com.amazonaws" % "aws-lambda-java-events" % "2.2.7",
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    ),
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
