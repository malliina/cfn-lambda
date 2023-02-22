package com.malliina.cdk

import buildinfo.BuildInfo
import software.amazon.awscdk.{Stack, Stage}
import software.amazon.awscdk.pipelines.{CodeBuildStep, CodePipeline, CodePipelineSource, ShellStep}
import software.amazon.awscdk.services.codecommit.Repository
import software.constructs.Construct

class PipelinesStack(scope: Construct, stackName: String)
  extends Stack(scope, stackName, CDK.stackProps)
  with CDKSyntax:
  val stack = this
  val cdkVersion = BuildInfo.cdkVersion
  val source = Repository.Builder
    .create(stack, "Source")
    .repositoryName(getStackName)
    .description(s"Code for $getStackName.")
    .build()
  val sourceInput = CodePipelineSource.codeCommit(source, "master")
  val pipeline = CodePipeline.Builder
    .create(stack, "Pipeline")
    .pipelineName(getStackName)
    .synth(
      ShellStep.Builder
        .create("Synth")
        .input(sourceInput)
        .commands(list(s"npm install -g aws-cdk@$cdkVersion", "cdk synth"))
        .build()
    )
    .build()

  val lambdaApp = pipeline.addStage(LambdaStage(stack, "qa"))
  lambdaApp.addPre(
    ShellStep.Builder
      .create("Build")
      .input(sourceInput)
      .primaryOutputDirectory("jartarget")
      .commands(
        list(
          "sbt sbtVersion",
          """sbt "project lambda" assembly""",
          "unzip lambda/function.jar -d jartarget"
        )
      )
      .build()
  )

class LambdaStage(scope: Construct, id: String) extends Stage(scope, id):
  val stack = LambdaStack(this, "LambdaStack")
